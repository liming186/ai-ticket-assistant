package com.example.aiticketassistant.domain.agent;

import java.util.List;

public interface AgentRepository {
    List<SupportAgent> findActiveAgents();
}
