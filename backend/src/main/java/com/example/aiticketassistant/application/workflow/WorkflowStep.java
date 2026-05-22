package com.example.aiticketassistant.application.workflow;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public record WorkflowStep(
        String name,
        String kind,
        WorkflowStepStatus status,
        Instant startedAt,
        Instant completedAt,
        Map<String, Object> input,
        Map<String, Object> output) {
    public WorkflowStep complete(Map<String, Object> output) {
        return new WorkflowStep(name, kind, WorkflowStepStatus.COMPLETED, startedAt, Instant.now(), input, output);
    }

    public WorkflowStep fail(Map<String, Object> output) {
        return new WorkflowStep(name, kind, WorkflowStepStatus.FAILED, startedAt, Instant.now(), input, output);
    }

    public long durationMillis() {
        Instant end = completedAt == null ? Instant.now() : completedAt;
        return Duration.between(startedAt, end).toMillis();
    }
}
