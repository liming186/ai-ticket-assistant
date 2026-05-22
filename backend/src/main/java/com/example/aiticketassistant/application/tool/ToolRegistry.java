package com.example.aiticketassistant.application.tool;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ToolRegistry {
    private final Map<String, ToolExecutor> executors = new LinkedHashMap<>();

    public ToolRegistry(List<ToolExecutor> toolExecutors) {
        toolExecutors.forEach(executor -> executors.put(executor.metadata().name(), executor));
    }

    public Optional<ToolExecutor> find(String name) {
        return Optional.ofNullable(executors.get(name));
    }

    public Collection<ToolMetadata> metadata() {
        return executors.values().stream()
                .map(ToolExecutor::metadata)
                .filter(metadata -> ToolNames.isAiCallable(metadata.name()))
                .toList();
    }

    public boolean exists(String name) {
        return executors.containsKey(name);
    }
}
