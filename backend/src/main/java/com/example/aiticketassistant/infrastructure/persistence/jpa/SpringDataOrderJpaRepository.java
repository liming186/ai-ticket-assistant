package com.example.aiticketassistant.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrderJpaRepository extends JpaRepository<OrderJpaEntity, String> {
    Optional<OrderJpaEntity> findByOrderNo(String orderNo);
    Optional<OrderJpaEntity> findTopByCustomerIdOrderByCreatedAtDesc(String customerId);
    List<OrderJpaEntity> findTop20ByCustomerIdOrderByCreatedAtDesc(String customerId);
}
