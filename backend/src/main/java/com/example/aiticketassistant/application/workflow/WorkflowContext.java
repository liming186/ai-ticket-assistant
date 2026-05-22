package com.example.aiticketassistant.application.workflow;

import com.example.aiticketassistant.application.agent.IntentResult;
import com.example.aiticketassistant.application.tool.ToolResult;
import com.example.aiticketassistant.domain.knowledge.KnowledgeSearchResult;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WorkflowContext {
    private final String sessionId;
    private final String customerId;
    private final String orderNo;
    private final String userMessage;
    private final WorkflowTrace trace = new WorkflowTrace();
    private IntentResult intent;
    private final List<ToolResult> toolResults = new ArrayList<>();
    private final List<KnowledgeSearchResult> knowledgeResults = new ArrayList<>();
    private final Map<String, Object> memory = new LinkedHashMap<>();

    public WorkflowContext(String sessionId, String customerId, String orderNo, String userMessage) {
        this.sessionId = sessionId;
        this.customerId = customerId;
        this.orderNo = orderNo;
        this.userMessage = userMessage;
    }

    public void remember(String key, Object value) {
        if (key != null && value != null) {
            memory.put(key, value);
        }
    }

    public Object recall(String key) {
        return memory.get(key);
    }

    public String sessionId() { return sessionId; }
    public String customerId() { return customerId; }
    public String orderNo() { return orderNo; }
    public String userMessage() { return userMessage; }
    public WorkflowTrace trace() { return trace; }
    public IntentResult intent() { return intent; }
    public void intent(IntentResult intent) { this.intent = intent; }
    public List<ToolResult> toolResults() { return toolResults; }
    public List<KnowledgeSearchResult> knowledgeResults() { return knowledgeResults; }
    public Map<String, Object> memory() { return memory; }
}
