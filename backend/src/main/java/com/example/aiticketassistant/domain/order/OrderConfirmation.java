package com.example.aiticketassistant.domain.order;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderConfirmation {
    private final String id;
    private final String traceId;
    private final String sessionId;
    private final String customerId;
    private final String productId;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal totalAmount;
    private final String addressId;
    private final String paymentMethodId;
    private OrderConfirmationStatus status;
    private final Instant expiresAt;
    private final Instant createdAt;
    private Instant updatedAt;

    public OrderConfirmation(String id, String traceId, String sessionId, String customerId, String productId, int quantity,
                             BigDecimal unitPrice, BigDecimal totalAmount, String addressId, String paymentMethodId,
                             OrderConfirmationStatus status, Instant expiresAt, Instant createdAt, Instant updatedAt) {
        this.id = require(id, "confirmation id");
        this.traceId = require(traceId, "trace id");
        this.sessionId = require(sessionId, "session id");
        this.customerId = require(customerId, "customer id");
        this.productId = require(productId, "product id");
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.addressId = require(addressId, "address id");
        this.paymentMethodId = require(paymentMethodId, "payment method id");
        this.status = status == null ? OrderConfirmationStatus.PENDING : status;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
        this.updatedAt = updatedAt == null ? this.createdAt : updatedAt;
    }

    public boolean expired(Instant now) {
        return !expiresAt.isAfter(now);
    }

    public void confirm() {
        status = OrderConfirmationStatus.CONFIRMED;
        updatedAt = Instant.now();
    }

    public void cancel() {
        status = OrderConfirmationStatus.CANCELLED;
        updatedAt = Instant.now();
    }

    public void fail() {
        status = OrderConfirmationStatus.FAILED;
        updatedAt = Instant.now();
    }

    public void expire() {
        status = OrderConfirmationStatus.EXPIRED;
        updatedAt = Instant.now();
    }

    private String require(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    public String id() { return id; }
    public String traceId() { return traceId; }
    public String sessionId() { return sessionId; }
    public String customerId() { return customerId; }
    public String productId() { return productId; }
    public int quantity() { return quantity; }
    public BigDecimal unitPrice() { return unitPrice; }
    public BigDecimal totalAmount() { return totalAmount; }
    public String addressId() { return addressId; }
    public String paymentMethodId() { return paymentMethodId; }
    public OrderConfirmationStatus status() { return status; }
    public Instant expiresAt() { return expiresAt; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
