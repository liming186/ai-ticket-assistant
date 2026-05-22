package com.example.aiticketassistant.domain.order;

import com.example.aiticketassistant.domain.ticket.TicketPriority;
import org.springframework.stereotype.Component;

@Component
public class OrderSupportPolicy {
    public TicketPriority priorityFor(Order order) {
        if (order == null) {
            return TicketPriority.MEDIUM;
        }
        if (order.isPaidButNotGenerated() || order.isPaymentFailed()) {
            return TicketPriority.HIGH;
        }
        return TicketPriority.MEDIUM;
    }
}
