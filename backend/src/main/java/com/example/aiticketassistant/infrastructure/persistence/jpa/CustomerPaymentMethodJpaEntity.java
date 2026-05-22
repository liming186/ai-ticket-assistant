package com.example.aiticketassistant.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "customer_payment_methods")
public class CustomerPaymentMethodJpaEntity {
    @Id
    private String id;
    @Column(nullable = false)
    private String customerId;
    @Column(nullable = false)
    private String methodType;
    @Column(nullable = false)
    private String displayLabel;
    @Column(name = "is_default", nullable = false)
    private boolean defaultMethod;
    @Column(nullable = false)
    private boolean active;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getMethodType() { return methodType; }
    public void setMethodType(String methodType) { this.methodType = methodType; }
    public String getDisplayLabel() { return displayLabel; }
    public void setDisplayLabel(String displayLabel) { this.displayLabel = displayLabel; }
    public boolean isDefaultMethod() { return defaultMethod; }
    public void setDefaultMethod(boolean defaultMethod) { this.defaultMethod = defaultMethod; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
