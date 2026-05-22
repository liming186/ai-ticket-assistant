package com.example.aiticketassistant.application.order;

import java.math.BigDecimal;
import java.util.Map;

public record OrderCreationResult(
        String orderNo,
        String orderStatus,
        String paymentStatus,
        BigDecimal amount,
        Map<String, Object> product,
        String message) {}
