package com.example.aiticketassistant.infrastructure.persistence.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCustomerPaymentMethodJpaRepository extends JpaRepository<CustomerPaymentMethodJpaEntity, String> {
    Optional<CustomerPaymentMethodJpaEntity> findFirstByCustomerIdAndDefaultMethodTrueAndActiveTrue(String customerId);
    Optional<CustomerPaymentMethodJpaEntity> findByIdAndCustomerIdAndActiveTrue(String id, String customerId);
}
