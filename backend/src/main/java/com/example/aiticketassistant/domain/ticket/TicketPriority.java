package com.example.aiticketassistant.domain.ticket;

public enum TicketPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT;

    public static TicketPriority normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return MEDIUM;
        }
        try {
            return TicketPriority.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return MEDIUM;
        }
    }
}
