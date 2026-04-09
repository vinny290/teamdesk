package com.teamdesk.server.signaling;

public enum SignalMessageType {
    REGISTER_AGENT,
    REGISTER_VIEWER,
    HEARTBEAT,
    SDP_OFFER,
    SDP_ANSWER,
    ICE_CANDIDATE,
    START_SESSION_ACK,
    INPUT_EVENT
}