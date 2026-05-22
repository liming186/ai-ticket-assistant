package com.example.aiticketassistant.domain.ticket;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TicketAggregateTest {
    @Test
    void opensAndResolvesTicketWithDomainBehavior() {
        Ticket ticket = Ticket.open("CUST-1", "支付失败", "用户支付失败", TicketCategory.PAYMENT, TicketPriority.HIGH);
        assertThat(ticket.status()).isEqualTo(TicketStatus.OPEN);
        assertThat(ticket.messages()).hasSize(1);

        ticket.resolve("已核验支付渠道，建议重试或退款");

        assertThat(ticket.status()).isEqualTo(TicketStatus.RESOLVED);
        assertThat(ticket.resolution()).contains("支付渠道");
    }
}
