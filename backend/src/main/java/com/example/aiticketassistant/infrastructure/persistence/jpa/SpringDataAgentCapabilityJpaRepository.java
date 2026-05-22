package com.example.aiticketassistant.infrastructure.persistence.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAgentCapabilityJpaRepository extends JpaRepository<AgentCapabilityJpaEntity, Long> {
    List<AgentCapabilityJpaEntity> findByAgentId(String agentId);
}
