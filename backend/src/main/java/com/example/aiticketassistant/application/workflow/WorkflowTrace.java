package com.example.aiticketassistant.application.workflow;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorkflowTrace {
    private final String traceId = "trace-" + UUID.randomUUID();
    private final Instant startedAt = Instant.now();
    private final List<WorkflowStep> steps = new ArrayList<>();

    public WorkflowStep startStep(String name, String kind, Map<String, Object> input) {
        WorkflowStep step = new WorkflowStep(name, kind, WorkflowStepStatus.STARTED, Instant.now(), null, input == null ? Map.of() : input, Map.of());
        steps.add(step);
        return step;
    }

    public void complete(WorkflowStep step, Map<String, Object> output) {
        replace(step, step.complete(output == null ? Map.of() : output));
    }

    public void fail(WorkflowStep step, Map<String, Object> output) {
        replace(step, step.fail(output == null ? Map.of() : output));
    }

    private void replace(WorkflowStep oldStep, WorkflowStep newStep) {
        int index = steps.indexOf(oldStep);
        if (index >= 0) {
            steps.set(index, newStep);
        }
    }

    public String traceId() { return traceId; }
    public Instant startedAt() { return startedAt; }
    public List<WorkflowStep> steps() { return Collections.unmodifiableList(steps); }
}
