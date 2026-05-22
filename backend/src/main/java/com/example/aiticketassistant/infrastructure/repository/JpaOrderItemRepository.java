package com.example.aiticketassistant.infrastructure.repository;

import com.example.aiticketassistant.domain.order.OrderItem;
import com.example.aiticketassistant.domain.order.OrderItemRepository;
import com.example.aiticketassistant.infrastructure.persistence.jpa.OrderItemJpaEntity;
import com.example.aiticketassistant.infrastructure.persistence.jpa.SpringDataOrderItemJpaRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaOrderItemRepository implements OrderItemRepository {
    private final SpringDataOrderItemJpaRepository repository;

    public JpaOrderItemRepository(SpringDataOrderItemJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrderItem save(OrderItem item) {
        return toDomain(repository.save(toEntity(item)));
    }

    @Override
    public List<OrderItem> findByOrderId(String orderId) {
        return repository.findByOrderId(orderId).stream().map(this::toDomain).toList();
    }

    private OrderItem toDomain(OrderItemJpaEntity entity) {
        return new OrderItem(entity.getOrderId(), entity.getProductId(), entity.getSku(), entity.getName(), entity.getQuantity(), entity.getPrice());
    }

    private OrderItemJpaEntity toEntity(OrderItem item) {
        Instant now = Instant.now();
        OrderItemJpaEntity entity = new OrderItemJpaEntity();
        entity.setOrderId(item.orderId());
        entity.setProductId(item.productId());
        entity.setSku(item.sku());
        entity.setName(item.name());
        entity.setQuantity(item.quantity());
        entity.setPrice(item.price());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return entity;
    }
}
