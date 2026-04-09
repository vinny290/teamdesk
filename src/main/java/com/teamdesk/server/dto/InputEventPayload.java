package com.teamdesk.server.dto;

import lombok.Data;

@Data
public class InputEventPayload {

    private String eventType;

    private Double x;
    private Double y;

    private Integer button;
    private Integer wheelDelta;

    private String key;
    private String code;
}