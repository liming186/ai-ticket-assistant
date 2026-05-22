package com.example.aiticketassistant.domain.catalog;

import java.math.BigDecimal;

public record Product(String id, String name, BigDecimal price, boolean active) {}
