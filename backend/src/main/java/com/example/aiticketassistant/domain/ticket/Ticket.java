package com.example.aiticketassistant.domain.ticket;

import com.example.aiticketassistant.domain.agent.AgentId;
import com.example.aiticketassistant.domain.shared.DomainException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ticket {
    private final TicketId id;
    private final String customerId;
    private String title;
    private String description;
    private TicketCategory category;
    private TicketPriority priority;
    private TicketStatus status;
    private AgentId assignedAgentId;
    private String resolution;
    private final List<TicketMessage> messages = new ArrayList<>();
    private final Instant createdAt;
    private Instant updatedAt;

    private Ticket(TicketId id, String customerId, String title, String description,
                   TicketCategory category, TicketPriority priority, Instant createdAt) {
        this.id = id;
        this.customerId = require(customerId, "customerId");
        this.title = require(title, "title");
        this.description = description == null ? "" : description;
        this.category = category == null ? TicketCategory.GENERAL : category;
        this.priority = priority == null ? TicketPriority.MEDIUM : priority;
        this.status = TicketStatus.OPEN;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
        this.updatedAt = this.createdAt;
    }

    public static Ticket open(String customerId, String title, String description,
                              TicketCategory category, TicketPriority priority) {
        Ticket ticket = new Ticket(TicketId.newId(), customerId, title, description, category, priority, Instant.now());
        ticket.addCustomerMessage(description == null || description.isBlank() ? title : description);
        return ticket;
    }

    public static Ticket restore(TicketId id, String customerId, String title, String description,
                                 TicketCategory category, TicketPriority priority, TicketStatus status,
                                 String assignedAgentId, String resolution, Instant createdAt, Instant updatedAt) {
        Ticket ticket = new Ticket(id, customerId, title, description, category, priority, createdAt);
        ticket.status = status == null ? TicketStatus.OPEN : status;
        ticket.assignedAgentId = assignedAgentId == null || assignedAgentId.isBlank() ? null : new AgentId(assignedAgentId);
        ticket.resolution = resolution;
        ticket.updatedAt = updatedAt == null ? createdAt : updatedAt;
        return ticket;
    }

    public void addCustomerMessage(String content) {
        ensureNotArchived();
        messages.add(new TicketMessage("customer", content, Instant.now()));
        touch();
    }

    public void assignTo(AgentId agentId) {
        ensureNotArchived();
        this.assignedAgentId = agentId;
        this.status = TicketStatus.IN_PROGRESS;
        touch();
    }

    public void markWaitingForCustomer() {
        ensureNotArchived();
        if (status == TicketStatus.RESOLVED) {
            throw new DomainException("Resolved ticket cannot wait for customer");
        }
        this.status = TicketStatus.WAITING_CUSTOMER;
        touch();
    }

    public void escalate(String reason) {
        ensureNotArchived();
        this.priority = priority == TicketPriority.URGENT ? TicketPriority.URGENT : TicketPriority.URGENT;
        messages.add(new TicketMessage("system", "Escalated: " + reason, Instant.now()));
        touch();
    }

    public void resolve(String resolution) {
        ensureNotArchived();
        this.resolution = require(resolution, "resolution");
        this.status = TicketStatus.RESOLVED;
        messages.add(new TicketMessage("assistant", resolution, Instant.now()));
        touch();
    }

    public void archive() {
        if (status != TicketStatus.RESOLVED) {
            throw new DomainException("Only resolved tickets can be archived");
        }
        this.status = TicketStatus.ARCHIVED;
        touch();
    }

    public void applyAiSuggestion(String suggestion) {
        ensureNotArchived();
        if (suggestion != null && !suggestion.isBlank()) {
            messages.add(new TicketMessage("ai-agent", suggestion, Instant.now()));
            touch();
        }
    }

    public boolean needsHumanEscalation() {
        return priority == TicketPriority.URGENT || category == TicketCategory.REFUND;
    }

    private void ensureNotArchived() {
        if (status == TicketStatus.ARCHIVED) {
            throw new DomainException("Archived ticket cannot be changed");
        }
    }

    private void touch() {
        updatedAt = Instant.now();
    }

    private static String require(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    public TicketId id() { return id; }
    public String customerId() { return customerId; }
    public String title() { return title; }
    public String description() { return description; }
    public TicketCategory category() { return category; }
    public TicketPriority priority() { return priority; }
    public TicketStatus status() { return status; }
    public AgentId assignedAgentId() { return assignedAgentId; }
    public String resolution() { return resolution; }
    public List<TicketMessage> messages() { return Collections.unmodifiableList(messages); }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
