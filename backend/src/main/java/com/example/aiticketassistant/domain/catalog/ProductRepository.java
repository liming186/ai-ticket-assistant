package com.example.aiticketassistant.domain.catalog;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(String id);
    List<Product> findActiveProducts();
    Optional<ProductInventory> findInventory(String productId);
    ProductInventory saveInventory(ProductInventory inventory);
}
