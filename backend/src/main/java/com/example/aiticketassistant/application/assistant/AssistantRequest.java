package com.example.aiticketassistant.application.assistant;

public record AssistantRequest(
        String sessionId,
        String customerId,
        String orderNo,
        String message) {
    public AssistantRequest {
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = "demo-session";
        }
        if (customerId == null || customerId.isBlank()) {
            customerId = "CUST-1001";
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
    }
}
