package com.example.aiticketassistant.domain.order;

import java.math.BigDecimal;

public record OrderItem(String orderId, String productId, String sku, String name, int quantity, BigDecimal price) {}
