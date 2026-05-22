package com.example.aiticketassistant.infrastructure.persistence.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrderItemJpaRepository extends JpaRepository<OrderItemJpaEntity, Long> {
    List<OrderItemJpaEntity> findByOrderId(String orderId);
}
