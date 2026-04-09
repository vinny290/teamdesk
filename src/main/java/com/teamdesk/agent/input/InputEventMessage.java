package com.teamdesk.agent.input;

import lombok.Data;

@Data
public class InputEventMessage {

    private String eventType;

    private Double x;
    private Double y;

    private Integer button;
    private Integer wheelDelta;

    private String key;
    private String code;
}