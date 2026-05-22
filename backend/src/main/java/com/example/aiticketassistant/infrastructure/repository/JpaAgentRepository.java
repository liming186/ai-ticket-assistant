package com.example.aiticketassistant.infrastructure.repository;

import com.example.aiticketassistant.domain.agent.AgentCapability;
import com.example.aiticketassistant.domain.agent.AgentId;
import com.example.aiticketassistant.domain.agent.AgentRepository;
import com.example.aiticketassistant.domain.agent.AgentRole;
import com.example.aiticketassistant.domain.agent.SupportAgent;
import com.example.aiticketassistant.infrastructure.persistence.jpa.AgentCapabilityJpaEntity;
import com.example.aiticketassistant.infrastructure.persistence.jpa.SpringDataAgentCapabilityJpaRepository;
import com.example.aiticketassistant.infrastructure.persistence.jpa.SpringDataSupportAgentJpaRepository;
import com.example.aiticketassistant.infrastructure.persistence.jpa.SupportAgentJpaEntity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class JpaAgentRepository implements AgentRepository {
    private final SpringDataSupportAgentJpaRepository agentRepository;
    private final SpringDataAgentCapabilityJpaRepository capabilityRepository;

    public JpaAgentRepository(SpringDataSupportAgentJpaRepository agentRepository, SpringDataAgentCapabilityJpaRepository capabilityRepository) {
        this.agentRepository = agentRepository;
        this.capabilityRepository = capabilityRepository;
    }

    @Override
    public List<SupportAgent> findActiveAgents() {
        return agentRepository.findByActiveTrue().stream().map(this::toDomain).toList();
    }

    private SupportAgent toDomain(SupportAgentJpaEntity entity) {
        Set<AgentCapability> capabilities = capabilityRepository.findByAgentId(entity.getId()).stream()
                .map(AgentCapabilityJpaEntity::getCapability)
                .map(AgentCapability::valueOf)
                .collect(Collectors.toSet());
        return new SupportAgent(new AgentId(entity.getId()), entity.getName(), AgentRole.valueOf(entity.getRole()), capabilities, entity.isActive());
    }
}
