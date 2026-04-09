package com.teamdesk.server.session;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "remote_sessions")
@Getter
@Setter
public class RemoteSession {

    @Id
    private String sessionId;

    private String machineId;

    private String viewerId;

    private Instant createdAt;

    private Instant startedAt;

    private Instant closedAt;

    private RemoteSessionStatus status;
}