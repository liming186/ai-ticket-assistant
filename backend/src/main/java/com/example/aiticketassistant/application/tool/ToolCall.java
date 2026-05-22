package com.example.aiticketassistant.application.tool;

import java.util.Map;

public record ToolCall(String tool, Map<String, Object> arguments) {}
