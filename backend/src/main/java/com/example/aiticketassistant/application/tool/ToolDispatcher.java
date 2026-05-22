package com.example.aiticketassistant.application.tool;

import com.example.aiticketassistant.application.workflow.WorkflowContext;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ToolDispatcher {
    private final ToolRegistry registry;
    private final ToolInvocationRecorder recorder;

    public ToolDispatcher(ToolRegistry registry, ToolInvocationRecorder recorder) {
        this.registry = registry;
        this.recorder = recorder;
    }

    public List<ToolResult> dispatchAll(List<ToolCall> calls, WorkflowContext context) {
        return calls.stream().map(call -> dispatch(call, context)).toList();
    }

    public ToolResult dispatch(ToolCall call, WorkflowContext context) {
        long start = System.currentTimeMillis();
        ToolResult result = !ToolNames.isAiCallable(call.tool())
                ? ToolResult.failure(call.tool(), "Tool is not AI-callable: " + call.tool())
                : registry.find(call.tool())
                .map(executor -> safeExecute(executor, call, context))
                .orElseGet(() -> ToolResult.failure(call.tool(), "Tool is not registered: " + call.tool()));
        recorder.record(context, call, result, System.currentTimeMillis() - start);
        context.toolResults().add(result);
        return result;
    }

    private ToolResult safeExecute(ToolExecutor executor, ToolCall call, WorkflowContext context) {
        try {
            return executor.execute(call.arguments(), context);
        } catch (Exception ex) {
            return ToolResult.failure(call.tool(), ex.getMessage());
        }
    }
}
