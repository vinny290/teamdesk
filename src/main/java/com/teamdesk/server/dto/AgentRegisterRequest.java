package com.teamdesk.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AgentRegisterRequest {

    @NotBlank
    private String machineId;

    @NotBlank
    private String hostname;

    private String osName;

    private String agentVersion;

    private String ip;
}