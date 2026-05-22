package com.example.aiticketassistant.application.query;

import com.example.aiticketassistant.domain.knowledge.KnowledgeSearchRepository;
import com.example.aiticketassistant.domain.knowledge.KnowledgeSearchResult;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchKnowledgeQueryHandler {
    private final KnowledgeSearchRepository repository;

    public SearchKnowledgeQueryHandler(KnowledgeSearchRepository repository) {
        this.repository = repository;
    }

    public List<KnowledgeSearchResult> handle(SearchKnowledgeQuery query) {
        return repository.search(query.query(), query.limit());
    }
}
