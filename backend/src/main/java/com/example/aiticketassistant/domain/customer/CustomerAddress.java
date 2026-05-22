package com.example.aiticketassistant.domain.customer;

public record CustomerAddress(
        String id,
        String customerId,
        String recipientName,
        String phone,
        String addressLine,
        boolean defaultAddress,
        boolean active) {
    public String display() {
        return recipientName + "，" + phone + "，" + addressLine;
    }
}
