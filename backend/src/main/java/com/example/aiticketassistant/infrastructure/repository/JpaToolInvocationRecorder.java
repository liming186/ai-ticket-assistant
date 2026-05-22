package com.example.aiticketassistant.infrastructure.repository;

import com.example.aiticketassistant.application.tool.ToolCall;
import com.example.aiticketassistant.application.tool.ToolInvocationRecorder;
import com.example.aiticketassistant.application.tool.ToolResult;
import com.example.aiticketassistant.application.workflow.WorkflowContext;
import com.example.aiticketassistant.infrastructure.persistence.jpa.SpringDataToolInvocationJpaRepository;
import com.example.aiticketassistant.infrastructure.persistence.jpa.ToolInvocationJpaEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class JpaToolInvocationRecorder implements ToolInvocationRecorder {
    private final SpringDataToolInvocationJpaRepository repository;
    private final ObjectMapper objectMapper;

    public JpaToolInvocationRecorder(SpringDataToolInvocationJpaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void record(WorkflowContext context, ToolCall call, ToolResult result, long durationMillis) {
        try {
            ToolInvocationJpaEntity entity = new ToolInvocationJpaEntity();
            entity.setTraceId(context.trace().traceId());
            entity.setSessionId(context.sessionId());
            entity.setToolName(call.tool());
            entity.setArgumentsJson(objectMapper.writeValueAsString(call.arguments()));
            entity.setSuccess(result.success());
            entity.setResultJson(objectMapper.writeValueAsString(result.success() ? result.data() : result.error()));
            entity.setDurationMillis(durationMillis);
            entity.setCreatedAt(Instant.now());
            repository.save(entity);
        } catch (Exception ignored) {
        }
    }
}
