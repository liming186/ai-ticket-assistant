package com.example.aiticketassistant.domain.knowledge;

import java.time.Instant;

public record KnowledgeDocument(
        String id,
        String title,
        String category,
        String source,
        String content,
        Instant updatedAt) {}
