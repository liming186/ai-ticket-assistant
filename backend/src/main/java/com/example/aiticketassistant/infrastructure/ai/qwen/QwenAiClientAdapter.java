package com.example.aiticketassistant.infrastructure.ai.qwen;

import com.example.aiticketassistant.application.agent.AiClientPort;
import com.example.aiticketassistant.application.agent.AiRequest;
import com.example.aiticketassistant.application.agent.AiResponse;
import com.example.aiticketassistant.infrastructure.config.AssistantProperties;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@ConditionalOnProperty(prefix = "assistant.ai", name = "provider", havingValue = "qwen")
public class QwenAiClientAdapter implements AiClientPort {
    private static final Logger log = LoggerFactory.getLogger(QwenAiClientAdapter.class);
    private static final String QWEN_CHAT_COMPLETIONS_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    private final AssistantProperties properties;
    private final WebClient webClient;

    public QwenAiClientAdapter(AssistantProperties properties, WebClient.Builder builder) {
        this.properties = properties;
        this.webClient = builder.build();
    }

    @Override
    public AiResponse complete(AiRequest request) {
        String apiKey = properties.ai().hasQwenApiKey() ? properties.ai().qwenApiKey() : System.getenv("QWEN_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            return AiResponse.fallback("Qwen API key is not configured. Fallback response from local guard.");
        }
        try {
            Map<String, Object> body = Map.of(
                    "model", properties.ai().qwenModel(),
                    "messages", List.of(
                            Map.of("role", "system", "content", request.systemPrompt()),
                            Map.of("role", "user", "content", request.userPrompt())),
                    "temperature", request.expectJson() ? 0.1 : 0.3,
                    "max_tokens", properties.ai().maxTokens());
            QwenChatResponse response = webClient.post()
                    .uri(QWEN_CHAT_COMPLETIONS_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(QwenChatResponse.class)
                    .block(Duration.ofSeconds(Math.max(5, properties.ai().timeout().toSeconds())));
            String text = response == null || response.choices() == null || response.choices().isEmpty()
                    ? ""
                    : response.choices().get(0).message().content();
            return new AiResponse(text, Map.of("model", properties.ai().qwenModel(), "provider", "qwen"), false);
        } catch (Exception ex) {
            log.warn("Qwen API call failed for agent {}: {}", request.agentName(), ex.getMessage());
            return AiResponse.fallback(fallbackFor(request));
        }
    }

    private String fallbackFor(AiRequest request) {
        if (request.expectJson()) {
            return "{\"intent\":\"GENERAL_SUPPORT\",\"title\":\"智能客服咨询\",\"priority\":\"MEDIUM\",\"confidence\":0.3,\"tool_calls\":[]}";
        }
        return "Qwen API 当前不可用，系统已降级到本地规则链路。";
    }

    public record QwenChatResponse(List<Choice> choices) {}
    public record Choice(Message message) {}
    public record Message(String content) {}
}
