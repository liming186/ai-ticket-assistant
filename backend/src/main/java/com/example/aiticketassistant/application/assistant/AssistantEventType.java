package com.example.aiticketassistant.application.assistant;

public enum AssistantEventType {
    WORKFLOW_STARTED("workflow_started"),
    AGENT_STARTED("agent_started"),
    AGENT_COMPLETED("agent_completed"),
    TOOL_CALL("tool_call"),
    TOOL_RESULT("tool_result"),
    KNOWLEDGE_SOURCES("knowledge_sources"),
    TICKET_CREATED("ticket_created"),
    TICKET_UPDATED("ticket_updated"),
    ORDER_CONFIRMATION_REQUIRED("order_confirmation_required"),
    ORDER_CREATED("order_created"),
    TRACE("trace"),
    FINAL("final"),
    ERROR("error");

    private final String wireName;

    AssistantEventType(String wireName) {
        this.wireName = wireName;
    }

    public String wireName() {
        return wireName;
    }
}
