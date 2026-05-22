package com.example.aiticketassistant.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductInventoryJpaRepository extends JpaRepository<ProductInventoryJpaEntity, String> {}
