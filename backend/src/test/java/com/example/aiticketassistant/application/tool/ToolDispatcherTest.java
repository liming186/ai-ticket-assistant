package com.example.aiticketassistant.application.tool;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.aiticketassistant.application.workflow.WorkflowContext;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ToolDispatcherTest {
    @Test
    void rejectsUnregisteredTools() {
        ToolRegistry registry = new ToolRegistry(List.of());
        ToolDispatcher dispatcher = new ToolDispatcher(registry, (context, call, result, duration) -> {});
        ToolResult result = dispatcher.dispatch(new ToolCall("UNKNOWN", Map.of()), new WorkflowContext("s", "c", null, "hello"));
        assertThat(result.success()).isFalse();
        assertThat(result.error()).contains("not AI-callable");
    }
}
