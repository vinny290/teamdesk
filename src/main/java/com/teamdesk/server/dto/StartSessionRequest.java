package com.teamdesk.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StartSessionRequest {

    @NotBlank
    private String machineId;

    @NotBlank
    private String viewerId;
}