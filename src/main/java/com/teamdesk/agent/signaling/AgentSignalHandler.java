package com.teamdesk.agent.signaling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamdesk.agent.input.InputEventMessage;
import com.teamdesk.agent.input.InputExecutor;
import com.teamdesk.server.signaling.SignalEnvelope;
import com.teamdesk.server.signaling.SignalMessageType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AgentSignalHandler {

    private final ObjectMapper objectMapper;
    private final InputExecutor inputExecutor;

    public void handle(SignalEnvelope envelope) throws Exception {
        if (envelope.getType() == SignalMessageType.INPUT_EVENT) {
            InputEventMessage event = objectMapper.readValue(
                    envelope.getPayload(),
                    InputEventMessage.class
            );

            switch (event.getEventType()) {
                case "mouse_move" -> {
                    if (event.getX() != null && event.getY() != null) {
                        inputExecutor.mouseMove(event.getX(), event.getY());
                    }
                }
                case "mouse_down" -> {
                    if (event.getButton() != null) {
                        if (event.getX() != null && event.getY() != null) {
                            inputExecutor.mouseMove(event.getX(), event.getY());
                        }
                        inputExecutor.mouseDown(event.getButton());
                    }
                }
                case "mouse_up" -> {
                    if (event.getButton() != null) {
                        if (event.getX() != null && event.getY() != null) {
                            inputExecutor.mouseMove(event.getX(), event.getY());
                        }
                        inputExecutor.mouseUp(event.getButton());
                    }
                }
                case "mouse_click" -> {
                    if (event.getButton() != null) {
                        if (event.getX() != null && event.getY() != null) {
                            inputExecutor.mouseMove(event.getX(), event.getY());
                        }
                        inputExecutor.mouseClick(event.getButton());
                    }
                }
                case "mouse_double_click" -> {
                    if (event.getButton() != null) {
                        if (event.getX() != null && event.getY() != null) {
                            inputExecutor.mouseMove(event.getX(), event.getY());
                        }
                        inputExecutor.mouseDoubleClick(event.getButton());
                    }
                }
                case "mouse_wheel" -> {
                    if (event.getWheelDelta() != null) {
                        inputExecutor.mouseWheel(event.getWheelDelta());
                    }
                }
                case "key_down" -> {
                    if (event.getKey() != null) {
                        inputExecutor.keyDown(event.getKey());
                    }
                }
                case "key_up" -> {
                    if (event.getKey() != null) {
                        inputExecutor.keyUp(event.getKey());
                    }
                }
            }
        }
    }
}