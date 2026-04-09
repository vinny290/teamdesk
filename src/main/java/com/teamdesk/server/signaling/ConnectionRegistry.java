package com.teamdesk.server.signaling;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionRegistry {

    private final Map<String, WebSocketSession> agentsByMachineId = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> viewersByViewerId = new ConcurrentHashMap<>();
    private final Map<String, String> machineBySessionId = new ConcurrentHashMap<>();
    private final Map<String, String> viewerBySessionId = new ConcurrentHashMap<>();

    public void registerAgent(String machineId, WebSocketSession session) {
        agentsByMachineId.put(machineId, session);
        machineBySessionId.put(session.getId(), machineId);
    }

    public void registerViewer(String viewerId, WebSocketSession session) {
        viewersByViewerId.put(viewerId, session);
        viewerBySessionId.put(session.getId(), viewerId);
    }

    public WebSocketSession getAgent(String machineId) {
        return agentsByMachineId.get(machineId);
    }

    public WebSocketSession getViewer(String viewerId) {
        return viewersByViewerId.get(viewerId);
    }

    public String findMachineIdBySession(String sessionId) {
        return machineBySessionId.get(sessionId);
    }

    public void remove(WebSocketSession session) {
        String machineId = machineBySessionId.remove(session.getId());
        if (machineId != null) {
            agentsByMachineId.remove(machineId);
        }

        String viewerId = viewerBySessionId.remove(session.getId());
        if (viewerId != null) {
            viewersByViewerId.remove(viewerId);
        }
    }
}