package com.example.aiticketassistant.application.agent;

import java.util.Map;

public record AiResponse(
        String text,
        Map<String, Object> metadata,
        boolean fallback) {
    public static AiResponse fallback(String text) {
        return new AiResponse(text, Map.of("fallback", true), true);
    }
}
