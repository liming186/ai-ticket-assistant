package com.example.aiticketassistant.domain.knowledge;

public record KnowledgeSearchResult(
        KnowledgeChunk chunk,
        double vectorScore,
        double bm25Score,
        double finalScore,
        String matchReason) {}
