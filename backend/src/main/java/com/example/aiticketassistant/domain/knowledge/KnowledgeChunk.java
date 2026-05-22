package com.example.aiticketassistant.domain.knowledge;

public record KnowledgeChunk(
        String id,
        String documentId,
        String title,
        String category,
        String source,
        String content) {}
