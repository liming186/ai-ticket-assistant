package com.example.aiticketassistant.application.tool;

import com.example.aiticketassistant.application.workflow.WorkflowContext;
import java.util.Map;

public interface ToolExecutor {
    ToolMetadata metadata();
    ToolResult execute(Map<String, Object> arguments, WorkflowContext context);
}
