package com.example.aiticketassistant.application.agent;

import java.util.Map;

public record AiRequest(
        String agentName,
        String systemPrompt,
        String userPrompt,
        Map<String, Object> structuredContext,
        boolean expectJson) {}
