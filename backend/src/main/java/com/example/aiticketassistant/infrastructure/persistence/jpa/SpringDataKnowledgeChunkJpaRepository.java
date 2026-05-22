package com.example.aiticketassistant.infrastructure.persistence.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataKnowledgeChunkJpaRepository extends JpaRepository<KnowledgeChunkJpaEntity, String> {
    List<KnowledgeChunkJpaEntity> findTop100ByOrderByIdAsc();
}
