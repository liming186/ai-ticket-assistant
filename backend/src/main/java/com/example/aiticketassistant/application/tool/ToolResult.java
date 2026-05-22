package com.example.aiticketassistant.application.tool;

import java.time.Instant;
import java.util.Map;

public record ToolResult(
        String tool,
        boolean success,
        Object data,
        String error,
        Instant executedAt) {
    public static ToolResult success(String tool, Object data) {
        return new ToolResult(tool, true, data, null, Instant.now());
    }

    public static ToolResult failure(String tool, String error) {
        return new ToolResult(tool, false, Map.of(), error, Instant.now());
    }
}
