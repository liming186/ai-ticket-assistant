package com.example.aiticketassistant.infrastructure.persistence.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCustomerAddressJpaRepository extends JpaRepository<CustomerAddressJpaEntity, String> {
    Optional<CustomerAddressJpaEntity> findFirstByCustomerIdAndDefaultAddressTrueAndActiveTrue(String customerId);
    Optional<CustomerAddressJpaEntity> findByIdAndCustomerIdAndActiveTrue(String id, String customerId);
}
