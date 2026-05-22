package com.example.aiticketassistant.domain.agent;

import com.example.aiticketassistant.domain.ticket.Ticket;
import com.example.aiticketassistant.domain.ticket.TicketCategory;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AgentRoutingService {
    private final AgentRepository repository;

    public AgentRoutingService(AgentRepository repository) {
        this.repository = repository;
    }

    public Optional<SupportAgent> route(Ticket ticket) {
        AgentCapability capability = capabilityFor(ticket.category());
        return repository.findActiveAgents().stream()
                .filter(agent -> agent.canHandle(capability))
                .findFirst();
    }

    private AgentCapability capabilityFor(TicketCategory category) {
        return switch (category) {
            case PAYMENT -> AgentCapability.PAYMENT;
            case ORDER -> AgentCapability.ORDER;
            case REFUND -> AgentCapability.REFUND;
            case LOGISTICS -> AgentCapability.LOGISTICS;
            default -> AgentCapability.ESCALATION;
        };
    }
}
