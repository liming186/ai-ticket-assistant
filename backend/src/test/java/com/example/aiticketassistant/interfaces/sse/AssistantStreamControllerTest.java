package com.example.aiticketassistant.interfaces.sse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.aiticketassistant.application.assistant.AssistantEvent;
import com.example.aiticketassistant.application.assistant.AssistantEventType;
import com.example.aiticketassistant.application.assistant.AssistantWorkflowService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class AssistantStreamControllerTest {
    @Test
    void mapsWorkflowEventsToSse() {
        AssistantWorkflowService service = mock(AssistantWorkflowService.class);
        when(service.stream(any())).thenReturn(Flux.just(AssistantEvent.of(AssistantEventType.FINAL, "ok", Map.of("done", true))));
        AssistantStreamController controller = new AssistantStreamController(service);
        var response = controller.stream("hello", "CUST", null, "S");
        assertThat(response.getHeaders().getCacheControl()).contains("no-cache");
        assertThat(response.getHeaders().getFirst("X-Accel-Buffering")).isEqualTo("no");
        StepVerifier.create(response.getBody())
                .assertNext(event -> {
                    assertThat(event).isInstanceOf(ServerSentEvent.class);
                    assertThat(event.event()).isEqualTo("final");
                })
                .verifyComplete();
    }
}
