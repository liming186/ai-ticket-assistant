package com.example.aiticketassistant.infrastructure.knowledge.chroma;

import com.example.aiticketassistant.infrastructure.config.AssistantProperties;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ChromaClient {
    private static final Logger log = LoggerFactory.getLogger(ChromaClient.class);
    private final WebClient webClient;
    private final AssistantProperties properties;

    public ChromaClient(WebClient.Builder builder, AssistantProperties properties) {
        this.properties = properties;
        this.webClient = builder.baseUrl(properties.chroma().baseUrl()).build();
    }

    public List<String> searchIds(String query, int limit) {
        try {
            webClient.get().uri("/api/v1/heartbeat").retrieve().bodyToMono(String.class).block();
        } catch (Exception ex) {
            log.debug("ChromaDB unavailable, falling back to local BM25 only: {}", ex.getMessage());
        }
        return List.of();
    }
}
