package com.example.aiticketassistant.domain.ticket;

import java.time.Instant;

public record TicketMessage(String author, String content, Instant createdAt) {
    public TicketMessage {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Ticket message content must not be blank");
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
