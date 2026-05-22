package com.example.aiticketassistant.interfaces.rest;

import com.example.aiticketassistant.application.query.SearchKnowledgeQuery;
import com.example.aiticketassistant.application.query.SearchKnowledgeQueryHandler;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {
    private final SearchKnowledgeQueryHandler handler;

    public KnowledgeController(SearchKnowledgeQueryHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/search")
    public List<Map<String, Object>> search(@RequestParam String query, @RequestParam(defaultValue = "5") int limit) {
        return handler.handle(new SearchKnowledgeQuery(query, limit)).stream()
                .map(result -> Map.<String, Object>of(
                        "id", result.chunk().id(),
                        "title", result.chunk().title(),
                        "source", result.chunk().source(),
                        "content", result.chunk().content(),
                        "score", result.finalScore()))
                .toList();
    }
}
