package com.teamdesk.server.signaling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamdesk.server.dto.AgentHeartbeatRequest;
import com.teamdesk.server.machine.MachineService;
import com.teamdesk.server.session.RemoteSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionRoutingService {

    private final ConnectionRegistry connectionRegistry;
    private final MachineService machineService;
    private final RemoteSessionService remoteSessionService;
    private final ObjectMapper objectMapper;

    public void handle(WebSocketSession source, SignalEnvelope envelope) throws Exception {
        switch (envelope.getType()) {
            case REGISTER_AGENT -> {
                connectionRegistry.registerAgent(envelope.getMachineId(), source);

                AgentHeartbeatRequest hb = new AgentHeartbeatRequest();
                hb.setMachineId(envelope.getMachineId());
                machineService.heartbeat(hb);

                log.info("Agent registered in signaling: machineId={}", envelope.getMachineId());
            }

            case REGISTER_VIEWER -> {
                connectionRegistry.registerViewer(envelope.getViewerId(), source);
                log.info("Viewer registered in signaling: viewerId={}", envelope.getViewerId());
            }

            case HEARTBEAT -> {
                AgentHeartbeatRequest hb = new AgentHeartbeatRequest();
                hb.setMachineId(envelope.getMachineId());
                machineService.heartbeat(hb);
                log.debug("Heartbeat from machineId={}", envelope.getMachineId());
            }

            case SDP_OFFER -> {
                log.info("Routing SDP_OFFER to agent. machineId={}, viewerId={}, sessionId={}",
                        envelope.getMachineId(), envelope.getViewerId(), envelope.getSessionId());

                WebSocketSession agent = connectionRegistry.getAgent(envelope.getMachineId());
                if (agent != null && agent.isOpen()) {
                    agent.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                } else {
                    log.warn("Agent session not found for machineId={}", envelope.getMachineId());
                }
            }

            case SDP_ANSWER -> {
                log.info("Routing SDP_ANSWER to viewer. machineId={}, viewerId={}, sessionId={}",
                        envelope.getMachineId(), envelope.getViewerId(), envelope.getSessionId());

                WebSocketSession viewer = connectionRegistry.getViewer(envelope.getViewerId());
                if (viewer != null && viewer.isOpen()) {
                    viewer.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                } else {
                    log.warn("Viewer session not found for viewerId={}", envelope.getViewerId());
                }
            }

            case ICE_CANDIDATE -> {
                if (source == connectionRegistry.getViewer(envelope.getViewerId())) {
                    log.debug("Routing ICE from viewer to agent. machineId={}, viewerId={}, sessionId={}",
                            envelope.getMachineId(), envelope.getViewerId(), envelope.getSessionId());

                    WebSocketSession agent = connectionRegistry.getAgent(envelope.getMachineId());
                    if (agent != null && agent.isOpen()) {
                        agent.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                    } else {
                        log.warn("Agent session not found for ICE. machineId={}", envelope.getMachineId());
                    }
                } else {
                    log.debug("Routing ICE from agent to viewer. machineId={}, viewerId={}, sessionId={}",
                            envelope.getMachineId(), envelope.getViewerId(), envelope.getSessionId());

                    WebSocketSession viewer = connectionRegistry.getViewer(envelope.getViewerId());
                    if (viewer != null && viewer.isOpen()) {
                        viewer.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                    } else {
                        log.warn("Viewer session not found for ICE. viewerId={}", envelope.getViewerId());
                    }
                }
            }

            case START_SESSION_ACK -> {
                log.info("Routing START_SESSION_ACK to viewer. sessionId={}, viewerId={}",
                        envelope.getSessionId(), envelope.getViewerId());

                WebSocketSession viewer = connectionRegistry.getViewer(envelope.getViewerId());
                if (viewer != null && viewer.isOpen()) {
                    viewer.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                }
                remoteSessionService.markActive(envelope.getSessionId());
            }

            case INPUT_EVENT -> {
                log.debug("Routing INPUT_EVENT to agent. machineId={}, viewerId={}, sessionId={}",
                        envelope.getMachineId(), envelope.getViewerId(), envelope.getSessionId());

                WebSocketSession agent = connectionRegistry.getAgent(envelope.getMachineId());
                if (agent != null && agent.isOpen()) {
                    agent.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                } else {
                    log.warn("Agent session not found for INPUT_EVENT. machineId={}", envelope.getMachineId());
                }
            }

            case UNREGISTER_AGENT -> {
                machineService.markOffline(envelope.getMachineId());
                log.info("Agent unregistered: machineId={}", envelope.getMachineId());
            }

            case REQUEST_CONSENT -> {
                validateConsentEnvelope(envelope);

                log.info("Routing REQUEST_CONSENT to agent. machineId={}, viewerId={}, sessionId={}",
                        envelope.getMachineId(), envelope.getViewerId(), envelope.getSessionId());

                WebSocketSession agent = connectionRegistry.getAgent(envelope.getMachineId());
                if (agent != null && agent.isOpen()) {
                    agent.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                } else {
                    log.warn("Agent session not found for REQUEST_CONSENT. machineId={}", envelope.getMachineId());
                }
            }

            case CONSENT_GRANTED, CONSENT_DECLINED -> {
                validateConsentEnvelope(envelope);

                log.info("Routing {} to viewer. machineId={}, viewerId={}, sessionId={}",
                        envelope.getType(), envelope.getMachineId(), envelope.getViewerId(), envelope.getSessionId());

                WebSocketSession viewer = connectionRegistry.getViewer(envelope.getViewerId());
                if (viewer != null && viewer.isOpen()) {
                    viewer.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                } else {
                    log.warn("Viewer session not found for consent response. viewerId={}", envelope.getViewerId());
                }
            }

            case STOP_SESSION -> {
                validateStopEnvelope(envelope);

                log.info("Handling STOP_SESSION. sessionId={}, machineId={}, viewerId={}",
                        envelope.getSessionId(), envelope.getMachineId(), envelope.getViewerId());

                remoteSessionService.close(envelope.getSessionId());

                boolean fromViewer = source == connectionRegistry.getViewer(envelope.getViewerId());
                boolean fromAgent = source == connectionRegistry.getAgent(envelope.getMachineId());

                if (fromViewer) {
                    WebSocketSession agent = connectionRegistry.getAgent(envelope.getMachineId());
                    if (agent != null && agent.isOpen()) {
                        agent.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                        log.info("STOP_SESSION routed from viewer to agent. sessionId={}", envelope.getSessionId());
                    } else {
                        log.warn("Agent session not found for STOP_SESSION. machineId={}", envelope.getMachineId());
                    }
                } else if (fromAgent) {
                    WebSocketSession viewer = connectionRegistry.getViewer(envelope.getViewerId());
                    if (viewer != null && viewer.isOpen()) {
                        viewer.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                        log.info("STOP_SESSION routed from agent to viewer. sessionId={}", envelope.getSessionId());
                    } else {
                        log.warn("Viewer session not found for STOP_SESSION. viewerId={}", envelope.getViewerId());
                    }
                } else {
                    log.warn("STOP_SESSION source is unknown. sessionId={}", envelope.getSessionId());
                }
            }
        }
    }

    private void validateConsentEnvelope(SignalEnvelope envelope) {
        if (isBlank(envelope.getMachineId())) {
            throw new IllegalArgumentException("machineId is required for consent flow");
        }
        if (isBlank(envelope.getViewerId())) {
            throw new IllegalArgumentException("viewerId is required for consent flow");
        }
    }

    private void validateStopEnvelope(SignalEnvelope envelope) {
        if (isBlank(envelope.getSessionId())) {
            throw new IllegalArgumentException("sessionId is required for stop session");
        }
        if (isBlank(envelope.getMachineId())) {
            throw new IllegalArgumentException("machineId is required for stop session");
        }
        if (isBlank(envelope.getViewerId())) {
            throw new IllegalArgumentException("viewerId is required for stop session");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}