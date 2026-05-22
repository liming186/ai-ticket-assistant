package com.example.aiticketassistant.domain.customer;

public record CustomerPaymentMethod(
        String id,
        String customerId,
        String methodType,
        String displayLabel,
        boolean defaultMethod,
        boolean active) {}
