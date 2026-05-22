package com.example.aiticketassistant.domain.order;

import java.math.BigDecimal;
import java.time.Instant;

public class Order {
    private final OrderId id;
    private final String orderNo;
    private final String customerId;
    private final BigDecimal amount;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private final String addressId;
    private final String paymentMethodId;
    private final String source;
    private final String confirmationId;
    private final Instant createdAt;
    private Instant updatedAt;

    public Order(OrderId id, String orderNo, String customerId, BigDecimal amount,
                 OrderStatus orderStatus, PaymentStatus paymentStatus, Instant createdAt, Instant updatedAt) {
        this(id, orderNo, customerId, amount, orderStatus, paymentStatus, null, null, null, null, createdAt, updatedAt);
    }

    public Order(OrderId id, String orderNo, String customerId, BigDecimal amount,
                 OrderStatus orderStatus, PaymentStatus paymentStatus, String addressId, String paymentMethodId,
                 String source, String confirmationId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.orderNo = orderNo;
        this.customerId = customerId;
        this.amount = amount == null ? BigDecimal.ZERO : amount;
        this.orderStatus = orderStatus == null ? OrderStatus.CREATED : orderStatus;
        this.paymentStatus = paymentStatus == null ? PaymentStatus.UNPAID : paymentStatus;
        this.addressId = addressId;
        this.paymentMethodId = paymentMethodId;
        this.source = source;
        this.confirmationId = confirmationId;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
        this.updatedAt = updatedAt == null ? this.createdAt : updatedAt;
    }

    public static Order assistantCreated(String id, String orderNo, String customerId, BigDecimal amount,
                                         String addressId, String paymentMethodId, String confirmationId) {
        Instant now = Instant.now();
        return new Order(new OrderId(id), orderNo, customerId, amount, OrderStatus.CREATED, PaymentStatus.UNPAID,
                addressId, paymentMethodId, "AI_ASSISTANT", confirmationId, now, now);
    }

    public boolean isPaidButNotGenerated() {
        return paymentStatus == PaymentStatus.PAID && orderStatus == OrderStatus.FAILED;
    }

    public boolean isPaymentFailed() {
        return paymentStatus == PaymentStatus.FAILED || orderStatus == OrderStatus.FAILED;
    }

    public boolean canCreateSupportTicket() {
        return isPaymentFailed() || isPaidButNotGenerated() || orderStatus == OrderStatus.CANCELLED;
    }

    public String aiBusinessSummary() {
        if (isPaidButNotGenerated()) {
            return "支付成功但订单生成失败，需要核验支付流水并补偿创建工单。";
        }
        if (isPaymentFailed()) {
            return "支付失败或订单异常，建议创建高优先级支付工单并引导用户重试或等待退款。";
        }
        return "订单状态为 " + orderStatus + "，支付状态为 " + paymentStatus + "。";
    }

    public void markFailed() {
        orderStatus = OrderStatus.FAILED;
        updatedAt = Instant.now();
    }

    public OrderId id() { return id; }
    public String orderNo() { return orderNo; }
    public String customerId() { return customerId; }
    public BigDecimal amount() { return amount; }
    public OrderStatus orderStatus() { return orderStatus; }
    public PaymentStatus paymentStatus() { return paymentStatus; }
    public String addressId() { return addressId; }
    public String paymentMethodId() { return paymentMethodId; }
    public String source() { return source; }
    public String confirmationId() { return confirmationId; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
