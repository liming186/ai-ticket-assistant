package com.example.aiticketassistant.application.assistant;

import java.time.Instant;
import java.util.Map;

public record AssistantEvent(
        AssistantEventType type,
        String message,
        Map<String, Object> payload,
        Instant timestamp) {
    public static AssistantEvent of(AssistantEventType type, String message, Map<String, Object> payload) {
        return new AssistantEvent(type, message, payload == null ? Map.of() : payload, Instant.now());
    }
}
