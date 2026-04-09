package com.teamdesk.server.machine;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "machines")
@Getter
@Setter
public class Machine {

    @Id
    private String machineId;

    private String hostname;

    private String osName;

    private String agentVersion;

    private String lastKnownIp;

    private Instant registeredAt;

    private Instant lastHeartbeatAt;

    @Enumerated(EnumType.STRING)
    private MachineStatus status;
}