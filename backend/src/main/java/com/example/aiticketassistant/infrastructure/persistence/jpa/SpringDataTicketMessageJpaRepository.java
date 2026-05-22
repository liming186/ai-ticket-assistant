package com.example.aiticketassistant.infrastructure.persistence.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTicketMessageJpaRepository extends JpaRepository<TicketMessageJpaEntity, Long> {
    List<TicketMessageJpaEntity> findByTicketIdOrderByCreatedAtAsc(String ticketId);
    void deleteByTicketId(String ticketId);
}
