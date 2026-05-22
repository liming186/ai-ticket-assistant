package com.example.aiticketassistant.domain.ticket;

public enum TicketCategory {
    PAYMENT,
    ORDER,
    LOGISTICS,
    REFUND,
    TECHNICAL,
    GENERAL;

    public static TicketCategory fromIntent(String intent) {
        if (intent == null) {
            return GENERAL;
        }
        String normalized = intent.toUpperCase();
        if (normalized.contains("PAY") || normalized.contains("支付")) {
            return PAYMENT;
        }
        if (normalized.contains("ORDER") || normalized.contains("订单")) {
            return ORDER;
        }
        if (normalized.contains("REFUND") || normalized.contains("退款")) {
            return REFUND;
        }
        return GENERAL;
    }
}
