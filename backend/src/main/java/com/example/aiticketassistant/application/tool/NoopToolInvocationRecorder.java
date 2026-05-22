package com.example.aiticketassistant.application.tool;

import com.example.aiticketassistant.application.workflow.WorkflowContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(ToolInvocationRecorder.class)
public class NoopToolInvocationRecorder implements ToolInvocationRecorder {
    @Override
    public void record(WorkflowContext context, ToolCall call, ToolResult result, long durationMillis) {
    }
}
