package com.example.aiticketassistant.domain.knowledge;

import java.util.List;

public interface KnowledgeSearchRepository {
    List<KnowledgeSearchResult> search(String query, int limit);
}
