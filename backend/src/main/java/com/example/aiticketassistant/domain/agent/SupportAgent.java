package com.example.aiticketassistant.domain.agent;

import java.util.Set;

public class SupportAgent {
    private final AgentId id;
    private final String name;
    private final AgentRole role;
    private final Set<AgentCapability> capabilities;
    private final boolean active;

    public SupportAgent(AgentId id, String name, AgentRole role, Set<AgentCapability> capabilities, boolean active) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.capabilities = capabilities == null ? Set.of() : Set.copyOf(capabilities);
        this.active = active;
    }

    public boolean canHandle(AgentCapability capability) {
        return active && capabilities.contains(capability);
    }

    public AgentId id() { return id; }
    public String name() { return name; }
    public AgentRole role() { return role; }
    public Set<AgentCapability> capabilities() { return capabilities; }
    public boolean active() { return active; }
}
