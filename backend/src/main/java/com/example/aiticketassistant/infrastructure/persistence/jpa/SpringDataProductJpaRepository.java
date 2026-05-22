package com.example.aiticketassistant.infrastructure.persistence.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductJpaRepository extends JpaRepository<ProductJpaEntity, String> {
    List<ProductJpaEntity> findByActiveTrueOrderById();
}
