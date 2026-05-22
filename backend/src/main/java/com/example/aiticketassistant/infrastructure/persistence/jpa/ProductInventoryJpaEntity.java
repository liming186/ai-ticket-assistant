package com.example.aiticketassistant.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "product_inventory")
public class ProductInventoryJpaEntity {
    @Id
    private String productId;
    @Column(nullable = false)
    private int stockOnHand;
    @Column(nullable = false)
    private int reservedStock;
    @Column(nullable = false)
    private Instant updatedAt;

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getStockOnHand() { return stockOnHand; }
    public void setStockOnHand(int stockOnHand) { this.stockOnHand = stockOnHand; }
    public int getReservedStock() { return reservedStock; }
    public void setReservedStock(int reservedStock) { this.reservedStock = reservedStock; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
