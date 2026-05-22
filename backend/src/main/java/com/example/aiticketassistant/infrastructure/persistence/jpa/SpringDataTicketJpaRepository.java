package com.example.aiticketassistant.infrastructure.persistence.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTicketJpaRepository extends JpaRepository<TicketJpaEntity, String> {
    List<TicketJpaEntity> findTop20ByCustomerIdOrderByUpdatedAtDesc(String customerId);
    List<TicketJpaEntity> findTop50ByStatusInOrderByUpdatedAtDesc(List<String> statuses);
}
