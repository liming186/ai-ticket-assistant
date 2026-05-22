package com.example.aiticketassistant.domain.ticket;

import org.springframework.stereotype.Component;

@Component
public class TicketEscalationPolicy {
    public boolean shouldEscalate(Ticket ticket, String latestUserMessage) {
        String text = latestUserMessage == null ? "" : latestUserMessage.toLowerCase();
        return ticket.priority() == TicketPriority.URGENT
                || text.contains("投诉")
                || text.contains("chargeback")
                || text.contains("legal")
                || ticket.category() == TicketCategory.REFUND;
    }
}
