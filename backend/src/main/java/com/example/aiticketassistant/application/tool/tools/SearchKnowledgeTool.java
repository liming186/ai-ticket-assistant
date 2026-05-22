package com.example.aiticketassistant.application.tool.tools;

import com.example.aiticketassistant.application.tool.ToolExecutor;
import com.example.aiticketassistant.application.tool.ToolMetadata;
import com.example.aiticketassistant.application.tool.ToolNames;
import com.example.aiticketassistant.application.tool.ToolResult;
import com.example.aiticketassistant.application.workflow.WorkflowContext;
import com.example.aiticketassistant.domain.knowledge.KnowledgeSearchRepository;
import com.example.aiticketassistant.domain.knowledge.KnowledgeSearchResult;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SearchKnowledgeTool implements ToolExecutor {
    private final KnowledgeSearchRepository searchRepository;

    public SearchKnowledgeTool(KnowledgeSearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public ToolMetadata metadata() {
        return new ToolMetadata(
                ToolNames.SEARCH_KNOWLEDGE,
                "Search FAQ, historical tickets, and documentation with hybrid vector + BM25 retrieval.",
                Map.of("query", "string", "limit", "integer"),
                Map.of("sources", "array"));
    }

    @Override
    public ToolResult execute(Map<String, Object> arguments, WorkflowContext context) {
        String query = String.valueOf(arguments.getOrDefault("query", context.userMessage()));
        int limit = parseLimit(arguments.get("limit"));
        List<KnowledgeSearchResult> results = searchRepository.search(query, limit);
        context.knowledgeResults().addAll(results);
        List<Map<String, Object>> sources = results.stream()
                .map(result -> Map.<String, Object>of(
                        "id", result.chunk().id(),
                        "title", result.chunk().title(),
                        "source", result.chunk().source(),
                        "content", result.chunk().content(),
                        "score", result.finalScore(),
                        "matchReason", result.matchReason()))
                .toList();
        return ToolResult.success(ToolNames.SEARCH_KNOWLEDGE, Map.of("sources", sources));
    }

    private int parseLimit(Object value) {
        if (value instanceof Number number) {
            return Math.max(1, Math.min(10, number.intValue()));
        }
        return 5;
    }
}
