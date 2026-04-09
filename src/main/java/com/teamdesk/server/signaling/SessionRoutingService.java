package com.teamdesk.server.signaling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamdesk.server.dto.AgentHeartbeatRequest;
import com.teamdesk.server.machine.MachineService;
import com.teamdesk.server.session.RemoteSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

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

                System.out.println("Agent registered in signaling: " + envelope.getMachineId());
            }

            case REGISTER_VIEWER -> {
                connectionRegistry.registerViewer(envelope.getViewerId(), source);
                System.out.println("Viewer registered in signaling: " + envelope.getViewerId());
            }

            case HEARTBEAT -> {
                AgentHeartbeatRequest hb = new AgentHeartbeatRequest();
                hb.setMachineId(envelope.getMachineId());
                machineService.heartbeat(hb);
                System.out.println("Heartbeat from: " + envelope.getMachineId());
            }

            case SDP_OFFER -> {
                System.out.println("Routing SDP_OFFER to agent: " + envelope.getMachineId());
                WebSocketSession agent = connectionRegistry.getAgent(envelope.getMachineId());
                if (agent != null && agent.isOpen()) {
                    agent.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                } else {
                    System.out.println("Agent session not found for machineId: " + envelope.getMachineId());
                }
            }

            case SDP_ANSWER -> {
                System.out.println("Routing SDP_ANSWER to viewer: " + envelope.getViewerId());
                WebSocketSession viewer = connectionRegistry.getViewer(envelope.getViewerId());
                if (viewer != null && viewer.isOpen()) {
                    viewer.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                } else {
                    System.out.println("Viewer session not found for viewerId: " + envelope.getViewerId());
                }
            }

            case ICE_CANDIDATE -> {
                if (source == connectionRegistry.getViewer(envelope.getViewerId())) {
                    System.out.println("Routing ICE from viewer to agent: " + envelope.getMachineId());
                    WebSocketSession agent = connectionRegistry.getAgent(envelope.getMachineId());
                    if (agent != null && agent.isOpen()) {
                        agent.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                    } else {
                        System.out.println("Agent session not found for ICE");
                    }
                } else {
                    System.out.println("Routing ICE from agent to viewer: " + envelope.getViewerId());
                    WebSocketSession viewer = connectionRegistry.getViewer(envelope.getViewerId());
                    if (viewer != null && viewer.isOpen()) {
                        viewer.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                    } else {
                        System.out.println("Viewer session not found for ICE");
                    }
                }
            }

            case START_SESSION_ACK -> {
                System.out.println("Routing START_SESSION_ACK to viewer: " + envelope.getViewerId());
                WebSocketSession viewer = connectionRegistry.getViewer(envelope.getViewerId());
                if (viewer != null && viewer.isOpen()) {
                    viewer.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                }
                remoteSessionService.markActive(envelope.getSessionId());
            }

            case INPUT_EVENT -> {
                System.out.println("Routing INPUT_EVENT to agent: " + envelope.getMachineId());
                WebSocketSession agent = connectionRegistry.getAgent(envelope.getMachineId());
                if (agent != null && agent.isOpen()) {
                    agent.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
                }
            }
        }
    }
}