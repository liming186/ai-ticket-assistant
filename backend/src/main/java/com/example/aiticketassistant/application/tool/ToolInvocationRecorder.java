package com.example.aiticketassistant.application.tool;

import com.example.aiticketassistant.application.workflow.WorkflowContext;

public interface ToolInvocationRecorder {
    void record(WorkflowContext context, ToolCall call, ToolResult result, long durationMillis);
}
