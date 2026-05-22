package com.example.aiticketassistant.application.tool;

import java.util.Map;

public record ToolMetadata(
        String name,
        String description,
        Map<String, Object> inputSchema,
        Map<String, Object> resultSchema) {}
