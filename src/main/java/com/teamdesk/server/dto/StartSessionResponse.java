package com.teamdesk.server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StartSessionResponse {
    private String sessionId;
    private String machineId;
    private String viewerId;
}