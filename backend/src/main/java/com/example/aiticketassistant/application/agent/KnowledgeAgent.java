package com.example.aiticketassistant.application.agent;

import com.example.aiticketassistant.application.workflow.WorkflowContext;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeAgent {
    private final AiClientPort aiClient;

    public KnowledgeAgent(AiClientPort aiClient) {
        this.aiClient = aiClient;
    }

    public String synthesize(WorkflowContext context) {
        String sources = context.knowledgeResults().stream()
                .map(result -> "- [%s] %s (score %.2f): %s".formatted(
                        result.chunk().title(), result.chunk().source(), result.finalScore(), result.chunk().content()))
                .collect(Collectors.joining("\n"));
        if (sources.isBlank()) {
            return "未检索到直接匹配的知识库资料，建议结合订单状态创建工单并人工复核。";
        }
        String system = """
                You are Knowledge Agent. Ground your answer in retrieved FAQ, historical tickets, and documents.
                Cite source titles. If sources are insufficient, say what is missing.
                """;
        String user = "User question: %s\nRetrieved sources:\n%s".formatted(context.userMessage(), sources);
        return aiClient.complete(new AiRequest("KnowledgeAgent", system, user, Map.of("sources", sources), false)).text();
    }
}
