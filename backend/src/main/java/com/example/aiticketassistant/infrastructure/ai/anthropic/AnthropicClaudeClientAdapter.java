package com.example.aiticketassistant.infrastructure.ai.anthropic;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.CacheControlEphemeral;
import com.anthropic.models.messages.ContentBlock;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.OutputConfig;
import com.anthropic.models.messages.TextBlockParam;
import com.anthropic.models.messages.ThinkingConfigAdaptive;
import com.anthropic.models.messages.ThinkingConfigParam;
import com.example.aiticketassistant.application.agent.AiClientPort;
import com.example.aiticketassistant.application.agent.AiRequest;
import com.example.aiticketassistant.application.agent.AiResponse;
import com.example.aiticketassistant.infrastructure.config.AssistantProperties;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "assistant.ai", name = "provider", havingValue = "anthropic")
public class AnthropicClaudeClientAdapter implements AiClientPort {
    private static final Logger log = LoggerFactory.getLogger(AnthropicClaudeClientAdapter.class);

    private final AssistantProperties properties;
    private final AnthropicClient client;

    public AnthropicClaudeClientAdapter(AssistantProperties properties) {
        this.properties = properties;
        this.client = properties.ai().hasApiKey()
                ? AnthropicOkHttpClient.builder().apiKey(properties.ai().anthropicApiKey()).build()
                : AnthropicOkHttpClient.fromEnv();
    }

    @Override
    public AiResponse complete(AiRequest request) {
        if (!properties.ai().hasApiKey() && (System.getenv("ANTHROPIC_API_KEY") == null || System.getenv("ANTHROPIC_API_KEY").isBlank())) {
            return AiResponse.fallback("Anthropic API key is not configured. Fallback response from local guard.");
        }
        try {
            TextBlockParam cachedSystem = TextBlockParam.builder()
                    .text(request.systemPrompt())
                    .cacheControl(CacheControlEphemeral.builder().build())
                    .build();
            MessageCreateParams params = MessageCreateParams.builder()
                    .model(properties.ai().model())
                    .maxTokens(properties.ai().maxTokens())
                    .systemOfTextBlockParams(java.util.List.of(cachedSystem))
                    .addUserMessage(request.userPrompt())
                    .thinking(ThinkingConfigParam.ofAdaptive(ThinkingConfigAdaptive.builder().build()))
                    .outputConfig(OutputConfig.builder().effort(OutputConfig.Effort.HIGH).build())
                    .cacheControl(CacheControlEphemeral.builder().build())
                    .build();
            Message message = client.messages().create(params);
            String text = message.content().stream()
                    .filter(ContentBlock::isText)
                    .map(block -> block.asText().text())
                    .collect(Collectors.joining("\n"));
            Map<String, Object> metadata = Map.of(
                    "model", message.model().asString(),
                    "inputTokens", message.usage().inputTokens(),
                    "outputTokens", message.usage().outputTokens(),
                    "cacheCreationInputTokens", message.usage().cacheCreationInputTokens().orElse(0L),
                    "cacheReadInputTokens", message.usage().cacheReadInputTokens().orElse(0L));
            return new AiResponse(text, metadata, false);
        } catch (Exception ex) {
            log.warn("Claude API call failed for agent {}: {}", request.agentName(), ex.getMessage());
            return AiResponse.fallback(fallbackFor(request));
        }
    }

    private String fallbackFor(AiRequest request) {
        if (request.expectJson()) {
            return "{\"intent\":\"GENERAL_SUPPORT\",\"title\":\"智能客服咨询\",\"priority\":\"MEDIUM\",\"confidence\":0.3,\"tool_calls\":[]}";
        }
        return "Claude API 当前不可用，系统已降级到本地规则链路。";
    }
}
