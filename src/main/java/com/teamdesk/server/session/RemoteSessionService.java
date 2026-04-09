package com.teamdesk.server.session;

import com.teamdesk.server.dto.StartSessionRequest;
import com.teamdesk.server.dto.StartSessionResponse;
import com.teamdesk.server.machine.MachineRepository;
import com.teamdesk.server.machine.MachineStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RemoteSessionService {

    private final RemoteSessionRepository remoteSessionRepository;
    private final MachineRepository machineRepository;

    @Transactional
    public StartSessionResponse start(StartSessionRequest request) {
        var machine = machineRepository.findById(request.getMachineId())
                .orElseThrow(() -> new IllegalArgumentException("Machine not found"));

        if (machine.getStatus() != MachineStatus.ONLINE) {
            throw new IllegalStateException("Machine is offline");
        }

        RemoteSession session = new RemoteSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setMachineId(request.getMachineId());
        session.setViewerId(request.getViewerId());
        session.setCreatedAt(Instant.now());
        session.setStatus(RemoteSessionStatus.CREATED);

        remoteSessionRepository.save(session);

        return StartSessionResponse.builder()
                .sessionId(session.getSessionId())
                .machineId(session.getMachineId())
                .viewerId(session.getViewerId())
                .build();
    }

    @Transactional
    public void markActive(String sessionId) {
        remoteSessionRepository.findById(sessionId).ifPresent(session -> {
            session.setStatus(RemoteSessionStatus.ACTIVE);
            session.setStartedAt(Instant.now());
            remoteSessionRepository.save(session);
        });
    }

    @Transactional
    public void close(String sessionId) {
        remoteSessionRepository.findById(sessionId).ifPresent(session -> {
            session.setStatus(RemoteSessionStatus.CLOSED);
            session.setClosedAt(Instant.now());
            remoteSessionRepository.save(session);
        });
    }
}