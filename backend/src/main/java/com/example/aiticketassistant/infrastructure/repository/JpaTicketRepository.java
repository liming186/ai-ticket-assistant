package com.example.aiticketassistant.infrastructure.repository;

import com.example.aiticketassistant.domain.ticket.Ticket;
import com.example.aiticketassistant.domain.ticket.TicketCategory;
import com.example.aiticketassistant.domain.ticket.TicketId;
import com.example.aiticketassistant.domain.ticket.TicketPriority;
import com.example.aiticketassistant.domain.ticket.TicketRepository;
import com.example.aiticketassistant.domain.ticket.TicketStatus;
import com.example.aiticketassistant.infrastructure.persistence.jpa.SpringDataTicketJpaRepository;
import com.example.aiticketassistant.infrastructure.persistence.jpa.SpringDataTicketMessageJpaRepository;
import com.example.aiticketassistant.infrastructure.persistence.jpa.TicketJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaTicketRepository implements TicketRepository {
    private final SpringDataTicketJpaRepository repository;
    private final SpringDataTicketMessageJpaRepository messageRepository;

    public JpaTicketRepository(SpringDataTicketJpaRepository repository, SpringDataTicketMessageJpaRepository messageRepository) {
        this.repository = repository;
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional
    public Ticket save(Ticket ticket) {
        TicketJpaEntity entity = toEntity(ticket);
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<Ticket> findById(TicketId id) {
        return repository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Ticket> findRecentByCustomerId(String customerId) {
        return repository.findTop20ByCustomerIdOrderByUpdatedAtDesc(customerId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Ticket> findOpenTickets() {
        return repository.findTop50ByStatusInOrderByUpdatedAtDesc(List.of("OPEN", "IN_PROGRESS", "WAITING_CUSTOMER"))
                .stream().map(this::toDomain).toList();
    }

    private TicketJpaEntity toEntity(Ticket ticket) {
        TicketJpaEntity entity = new TicketJpaEntity();
        entity.setId(ticket.id().value());
        entity.setCustomerId(ticket.customerId());
        entity.setTitle(ticket.title());
        entity.setDescription(ticket.description());
        entity.setCategory(ticket.category().name());
        entity.setPriority(ticket.priority().name());
        entity.setStatus(ticket.status().name());
        entity.setAssignedAgentId(ticket.assignedAgentId() == null ? null : ticket.assignedAgentId().value());
        entity.setResolution(ticket.resolution());
        entity.setCreatedAt(ticket.createdAt());
        entity.setUpdatedAt(ticket.updatedAt());
        return entity;
    }

    private Ticket toDomain(TicketJpaEntity entity) {
        return Ticket.restore(
                new TicketId(entity.getId()),
                entity.getCustomerId(),
                entity.getTitle(),
                entity.getDescription(),
                TicketCategory.valueOf(entity.getCategory()),
                TicketPriority.valueOf(entity.getPriority()),
                TicketStatus.valueOf(entity.getStatus()),
                entity.getAssignedAgentId(),
                entity.getResolution(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
