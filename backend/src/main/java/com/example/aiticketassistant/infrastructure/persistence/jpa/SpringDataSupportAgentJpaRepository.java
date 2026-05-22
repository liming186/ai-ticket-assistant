package com.example.aiticketassistant.infrastructure.persistence.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSupportAgentJpaRepository extends JpaRepository<SupportAgentJpaEntity, String> {
    List<SupportAgentJpaEntity> findByActiveTrue();
}
