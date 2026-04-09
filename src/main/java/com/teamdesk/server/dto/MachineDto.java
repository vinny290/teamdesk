package com.teamdesk.server.dto;

import com.teamdesk.server.machine.MachineStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MachineDto {
    private String machineId;
    private String hostname;
    private String osName;
    private String agentVersion;
    private String lastKnownIp;
    private Instant registeredAt;
    private Instant lastHeartbeatAt;
    private MachineStatus status;
}