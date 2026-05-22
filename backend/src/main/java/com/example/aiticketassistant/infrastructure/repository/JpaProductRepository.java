package com.example.aiticketassistant.infrastructure.repository;

import com.example.aiticketassistant.domain.catalog.Product;
import com.example.aiticketassistant.domain.catalog.ProductInventory;
import com.example.aiticketassistant.domain.catalog.ProductRepository;
import com.example.aiticketassistant.infrastructure.persistence.jpa.ProductInventoryJpaEntity;
import com.example.aiticketassistant.infrastructure.persistence.jpa.ProductJpaEntity;
import com.example.aiticketassistant.infrastructure.persistence.jpa.SpringDataProductInventoryJpaRepository;
import com.example.aiticketassistant.infrastructure.persistence.jpa.SpringDataProductJpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaProductRepository implements ProductRepository {
    private final SpringDataProductJpaRepository productRepository;
    private final SpringDataProductInventoryJpaRepository inventoryRepository;

    public JpaProductRepository(SpringDataProductJpaRepository productRepository,
                                SpringDataProductInventoryJpaRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Optional<Product> findById(String id) {
        return productRepository.findById(id).map(this::toProduct);
    }

    @Override
    public List<Product> findActiveProducts() {
        return productRepository.findByActiveTrueOrderById().stream().map(this::toProduct).toList();
    }

    @Override
    public Optional<ProductInventory> findInventory(String productId) {
        return inventoryRepository.findById(productId).map(this::toInventory);
    }

    @Override
    public ProductInventory saveInventory(ProductInventory inventory) {
        return toInventory(inventoryRepository.save(toEntity(inventory)));
    }

    private Product toProduct(ProductJpaEntity entity) {
        return new Product(entity.getId(), entity.getName(), entity.getPrice(), entity.isActive());
    }

    private ProductInventory toInventory(ProductInventoryJpaEntity entity) {
        return new ProductInventory(entity.getProductId(), entity.getStockOnHand(), entity.getReservedStock());
    }

    private ProductInventoryJpaEntity toEntity(ProductInventory inventory) {
        ProductInventoryJpaEntity entity = new ProductInventoryJpaEntity();
        entity.setProductId(inventory.productId());
        entity.setStockOnHand(inventory.stockOnHand());
        entity.setReservedStock(inventory.reservedStock());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }
}
