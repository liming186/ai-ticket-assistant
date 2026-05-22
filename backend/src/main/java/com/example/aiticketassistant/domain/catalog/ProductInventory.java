package com.example.aiticketassistant.domain.catalog;

public record ProductInventory(String productId, int stockOnHand, int reservedStock) {
    public int availableStock() {
        return stockOnHand - reservedStock;
    }

    public ProductInventory reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (availableStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for product: " + productId);
        }
        return new ProductInventory(productId, stockOnHand, reservedStock + quantity);
    }
}
