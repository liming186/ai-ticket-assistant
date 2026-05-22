package com.example.aiticketassistant.application.assistant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.aiticketassistant.application.agent.AiClientPort;
import com.example.aiticketassistant.application.agent.AiRequest;
import com.example.aiticketassistant.application.agent.AiResponse;
import com.example.aiticketassistant.application.agent.IntentAgent;
import com.example.aiticketassistant.application.agent.IntentResult;
import com.example.aiticketassistant.application.agent.IntentType;
import com.example.aiticketassistant.application.agent.KnowledgeAgent;
import com.example.aiticketassistant.application.agent.OrderAgent;
import com.example.aiticketassistant.application.assembler.AssistantResponseAssembler;
import com.example.aiticketassistant.application.tool.ToolCall;
import com.example.aiticketassistant.application.tool.ToolDispatcher;
import com.example.aiticketassistant.application.tool.ToolNames;
import com.example.aiticketassistant.application.tool.ToolRegistry;
import com.example.aiticketassistant.domain.ticket.TicketPriority;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class AssistantWorkflowServiceTest {
    @Test
    void budgetQuestionUsesDirectQwenOnly() {
        TestFixture fixture = new TestFixture();
        when(fixture.aiClient.complete(any())).thenReturn(AiResponse.fallback("1000元最多可以买9件T恤。"));

        List<AssistantEvent> events = fixture.service.stream(request("我有1000元，最多可以买几件衣服？")).collectList().block();

        assertThat(events).isNotNull();
        assertThat(events).extracting(AssistantEvent::type).containsOnly(AssistantEventType.FINAL);
        assertThat(events.get(events.size() - 1).payload()).containsEntry("done", true);
        ArgumentCaptor<AiRequest> request = forClass(AiRequest.class);
        verify(fixture.aiClient).complete(request.capture());
        assertThat(request.getValue().agentName()).isEqualTo("DirectQwen");
        assertThat(request.getValue().expectJson()).isFalse();
        verify(fixture.intentAgent, never()).analyze(any(), anyString());
        verify(fixture.toolDispatcher, never()).dispatchAll(any(), any());
    }

    @Test
    void genericOrderQuestionUsesDirectQwenOnly() {
        TestFixture fixture = new TestFixture();
        when(fixture.aiClient.complete(any())).thenReturn(AiResponse.fallback("建议先查看支付流水并联系客服核验。"));

        List<AssistantEvent> events = fixture.service.stream(request("支付成功但订单没生成怎么办？")).collectList().block();

        assertThat(events).isNotNull();
        assertThat(events).extracting(AssistantEvent::type)
                .containsOnly(AssistantEventType.FINAL)
                .doesNotContain(AssistantEventType.AGENT_STARTED, AssistantEventType.TOOL_CALL);
        verify(fixture.intentAgent, never()).analyze(any(), anyString());
        verify(fixture.toolDispatcher, never()).dispatchAll(any(), any());
    }

    @Test
    void userOrderQueryUsesAgentWorkflow() {
        TestFixture fixture = new TestFixture();
        when(fixture.intentAgent.analyze(any(), anyString())).thenReturn(new IntentResult(
                IntentType.QUERY_ORDER,
                "订单查询",
                TicketPriority.MEDIUM,
                0.9,
                List.of(new ToolCall(ToolNames.QUERY_ORDER, Map.of("customerId", "CUST-1001")))));
        when(fixture.toolDispatcher.dispatchAll(any(), any())).thenReturn(List.of());
        when(fixture.responseAssembler.assemble(any(), any(), any())).thenReturn("已进入订单查询流程。\n");

        List<AssistantEvent> events = fixture.service.stream(request("帮我查一下我的订单")).collectList().block();

        assertThat(events).isNotNull();
        assertThat(events).extracting(AssistantEvent::type)
                .contains(AssistantEventType.WORKFLOW_STARTED, AssistantEventType.AGENT_STARTED, AssistantEventType.TOOL_CALL, AssistantEventType.TRACE);
        verify(fixture.intentAgent).analyze(any(), anyString());
        verify(fixture.toolDispatcher).dispatchAll(any(), any());
    }

    @Test
    void catalogPriceQuestionUsesAgentWorkflow() {
        TestFixture fixture = new TestFixture();
        when(fixture.intentAgent.analyze(any(), anyString())).thenReturn(new IntentResult(
                IntentType.KNOWLEDGE_QA,
                "商品目录查询",
                TicketPriority.MEDIUM,
                0.9,
                List.of(new ToolCall(ToolNames.SEARCH_KNOWLEDGE, Map.of("query", "价格刚好699的衣服是什么", "limit", 5)))));
        when(fixture.toolDispatcher.dispatchAll(any(), any())).thenReturn(List.of());
        when(fixture.responseAssembler.assemble(any(), any(), any())).thenReturn("价格刚好699元的是羊毛混纺大衣。\n");

        List<AssistantEvent> events = fixture.service.stream(request("价格刚好699的衣服是什么")).collectList().block();

        assertThat(events).isNotNull();
        assertThat(events).extracting(AssistantEvent::type)
                .contains(AssistantEventType.WORKFLOW_STARTED, AssistantEventType.AGENT_STARTED, AssistantEventType.TOOL_CALL, AssistantEventType.TRACE);
        verify(fixture.intentAgent).analyze(any(), anyString());
        verify(fixture.toolDispatcher).dispatchAll(any(), any());
    }

    @Test
    void catalogBudgetQuestionUsesAgentWorkflow() {
        TestFixture fixture = new TestFixture();
        when(fixture.intentAgent.analyze(any(), anyString())).thenReturn(new IntentResult(
                IntentType.KNOWLEDGE_QA,
                "商品目录筛选",
                TicketPriority.MEDIUM,
                0.9,
                List.of(new ToolCall(ToolNames.SEARCH_KNOWLEDGE, Map.of("query", "我的衣服比699便宜的有哪几种，我预算有限", "limit", 5)))));
        when(fixture.toolDispatcher.dispatchAll(any(), any())).thenReturn(List.of());
        when(fixture.responseAssembler.assemble(any(), any(), any())).thenReturn("比699便宜的衣服有多种。\n");

        List<AssistantEvent> events = fixture.service.stream(request("我的衣服比699便宜的有哪几种，我预算有限")).collectList().block();

        assertThat(events).isNotNull();
        assertThat(events).extracting(AssistantEvent::type)
                .contains(AssistantEventType.WORKFLOW_STARTED, AssistantEventType.AGENT_STARTED, AssistantEventType.TOOL_CALL, AssistantEventType.TRACE);
        verify(fixture.intentAgent).analyze(any(), anyString());
        verify(fixture.toolDispatcher).dispatchAll(any(), any());
    }

    @Test
    void purchaseSynonymUsesAgentWorkflow() {
        TestFixture fixture = new TestFixture();
        when(fixture.intentAgent.analyze(any(), anyString())).thenReturn(new IntentResult(
                IntentType.GENERAL_SUPPORT,
                "安全下单确认",
                TicketPriority.MEDIUM,
                0.9,
                List.of(new ToolCall(ToolNames.CREATE_ORDER, Map.of("productId", "CLOTH-HOODIE-004", "quantity", 1)))));
        when(fixture.toolDispatcher.dispatchAll(any(), any())).thenReturn(List.of());
        when(fixture.responseAssembler.assemble(any(), any(), any())).thenReturn("已进入下单确认流程。\n");

        List<AssistantEvent> events = fixture.service.stream(request("来一件黑色卫衣")).collectList().block();

        assertThat(events).isNotNull();
        assertThat(events).extracting(AssistantEvent::type)
                .contains(AssistantEventType.WORKFLOW_STARTED, AssistantEventType.AGENT_STARTED, AssistantEventType.TOOL_CALL, AssistantEventType.TRACE);
        verify(fixture.intentAgent).analyze(any(), anyString());
        verify(fixture.toolDispatcher).dispatchAll(any(), any());
    }

    @Test
    void purchaseActionUsesAgentWorkflow() {
        TestFixture fixture = new TestFixture();
        when(fixture.intentAgent.analyze(any(), anyString())).thenReturn(new IntentResult(
                IntentType.GENERAL_SUPPORT,
                "安全下单确认",
                TicketPriority.MEDIUM,
                0.9,
                List.of(new ToolCall(ToolNames.CREATE_ORDER, Map.of("productId", "CLOTH-TEE-001", "quantity", 1)))));
        when(fixture.toolDispatcher.dispatchAll(any(), any())).thenReturn(List.of());
        when(fixture.responseAssembler.assemble(any(), any(), any())).thenReturn("已进入下单确认流程。\n");

        List<AssistantEvent> events = fixture.service.stream(request("我要买一件黑色卫衣")).collectList().block();

        assertThat(events).isNotNull();
        assertThat(events).extracting(AssistantEvent::type)
                .contains(AssistantEventType.WORKFLOW_STARTED, AssistantEventType.AGENT_STARTED, AssistantEventType.TOOL_CALL, AssistantEventType.TRACE);
        verify(fixture.intentAgent).analyze(any(), anyString());
        verify(fixture.toolDispatcher).dispatchAll(any(), any());
    }

    private static AssistantRequest request(String message) {
        return new AssistantRequest("s", "CUST-1001", null, message);
    }

    private static class TestFixture {
        private final IntentAgent intentAgent = mock(IntentAgent.class);
        private final OrderAgent orderAgent = mock(OrderAgent.class);
        private final KnowledgeAgent knowledgeAgent = mock(KnowledgeAgent.class);
        private final ToolDispatcher toolDispatcher = mock(ToolDispatcher.class);
        private final ToolRegistry toolRegistry = mock(ToolRegistry.class);
        private final AssistantResponseAssembler responseAssembler = mock(AssistantResponseAssembler.class);
        private final AiClientPort aiClient = mock(AiClientPort.class);
        private final AssistantWorkflowService service;

        private TestFixture() {
            when(toolRegistry.metadata()).thenReturn(List.of());
            service = new AssistantWorkflowService(
                intentAgent,
                orderAgent,
                knowledgeAgent,
                toolDispatcher,
                toolRegistry,
                    responseAssembler,
                    aiClient);
        }
    }
}
