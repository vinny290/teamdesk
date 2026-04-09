package com.teamdesk.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AgentHeartbeatRequest {

    @NotBlank
    private String machineId;

    private String ip;
}