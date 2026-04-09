package com.teamdesk.server.signaling;

import lombok.Data;

@Data
public class SignalEnvelope {

    private SignalMessageType type;

    private String sessionId;
    private String machineId;
    private String viewerId;

    private String sdp;
    private String candidate;
    private String sdpMid;
    private Integer sdpMLineIndex;

    private String payload;
}