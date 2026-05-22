package com.example.aiticketassistant.domain.ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {
    Ticket save(Ticket ticket);
    Optional<Ticket> findById(TicketId id);
    List<Ticket> findRecentByCustomerId(String customerId);
    List<Ticket> findOpenTickets();
}
