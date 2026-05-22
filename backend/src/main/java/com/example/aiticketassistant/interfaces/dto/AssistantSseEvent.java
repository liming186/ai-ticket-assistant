package com.example.aiticketassistant.interfaces.dto;

import java.time.Instant;
import java.util.Map;

public record AssistantSseEvent(String type, String message, Map<String, Object> payload, Instant timestamp) {}
