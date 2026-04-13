package com.teamdesk.server.signaling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamdesk.server.machine.MachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignalingSocketHandler extends TextWebSocketHandler {

    private final SessionRoutingService sessionRoutingService;
    private final ConnectionRegistry connectionRegistry;
    private final MachineService machineService;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WS connected: sessionId={}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        SignalEnvelope envelope = objectMapper.readValue(message.getPayload(), SignalEnvelope.class);
        sessionRoutingService.handle(session, envelope);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String machineId = connectionRegistry.findMachineIdBySession(session.getId());
        if (machineId != null) {
            machineService.markOffline(machineId);
        }

        connectionRegistry.remove(session);
        log.info("WS closed: sessionId={}, code={}, reason={}",
                session.getId(), status.getCode(), status.getReason());
    }
}