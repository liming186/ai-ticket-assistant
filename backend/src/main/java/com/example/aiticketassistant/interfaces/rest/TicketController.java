package com.example.aiticketassistant.interfaces.rest;

import com.example.aiticketassistant.domain.shared.NotFoundException;
import com.example.aiticketassistant.domain.ticket.Ticket;
import com.example.aiticketassistant.domain.ticket.TicketId;
import com.example.aiticketassistant.domain.ticket.TicketRepository;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketRepository repository;

    public TicketController(TicketRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return repository.findOpenTickets().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable String id) {
        return repository.findById(new TicketId(id)).map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Ticket not found: " + id));
    }

    private Map<String, Object> toDto(Ticket ticket) {
        return Map.of(
                "id", ticket.id().value(),
                "customerId", ticket.customerId(),
                "title", ticket.title(),
                "category", ticket.category().name(),
                "priority", ticket.priority().name(),
                "status", ticket.status().name(),
                "updatedAt", ticket.updatedAt());
    }
}
