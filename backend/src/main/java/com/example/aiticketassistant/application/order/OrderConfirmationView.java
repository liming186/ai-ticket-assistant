package com.example.aiticketassistant.application.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record OrderConfirmationView(
        String confirmationId,
        String sessionId,
        String customerId,
        String message,
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        Map<String, Object> address,
        Map<String, Object> paymentMethod,
        Instant expiresAt) {}
