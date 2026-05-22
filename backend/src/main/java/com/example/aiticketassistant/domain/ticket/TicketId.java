package com.example.aiticketassistant.domain.ticket;

import java.util.UUID;

public record TicketId(String value) {
    public TicketId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TicketId must not be blank");
        }
    }

    public static TicketId newId() {
        return new TicketId("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
}
