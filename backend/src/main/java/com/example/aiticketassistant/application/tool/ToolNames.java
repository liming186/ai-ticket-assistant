package com.example.aiticketassistant.application.tool;

import java.util.Set;

public final class ToolNames {
    public static final String CREATE_TICKET = "CREATE_TICKET";
    public static final String QUERY_ORDER = "QUERY_ORDER";
    public static final String UPDATE_TICKET = "UPDATE_TICKET";
    public static final String SEARCH_KNOWLEDGE = "SEARCH_KNOWLEDGE";
    public static final String SEARCH_PRODUCTS = "SEARCH_PRODUCTS";
    public static final String CREATE_ORDER = "CREATE_ORDER";

    private static final Set<String> AI_CALLABLE_TOOLS = Set.of(
            CREATE_TICKET,
            QUERY_ORDER,
            UPDATE_TICKET,
            SEARCH_KNOWLEDGE,
            SEARCH_PRODUCTS,
            CREATE_ORDER);

    public static boolean isAiCallable(String toolName) {
        return AI_CALLABLE_TOOLS.contains(toolName);
    }

    private ToolNames() {}
}
