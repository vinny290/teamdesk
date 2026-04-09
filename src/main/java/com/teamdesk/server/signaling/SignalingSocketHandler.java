package com.teamdesk.server.signaling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamdesk.server.machine.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class SignalingSocketHandler extends TextWebSocketHandler {

    private final SessionRoutingService sessionRoutingService;
    private final ConnectionRegistry connectionRegistry;
    private final MachineService machineService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("WS connected: " + session.getId());
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
    }
}