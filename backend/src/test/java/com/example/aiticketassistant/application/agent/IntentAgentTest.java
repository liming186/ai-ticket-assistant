package com.example.aiticketassistant.application.agent;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.aiticketassistant.application.tool.ToolNames;
import com.example.aiticketassistant.application.workflow.WorkflowContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class IntentAgentTest {
    @Test
    void ignoresProductSearchToolForBudgetQuestion() {
        AiClientPort fakeAi = request -> AiResponse.fallback("""
                {"intent":"GENERAL_SUPPORT","title":"预算买衣服","priority":"MEDIUM","confidence":0.95,"tool_calls":[{"tool":"SEARCH_PRODUCTS","arguments":{"maxPrice":1000,"category":"clothing"}}]}
                """);
        IntentAgent agent = new IntentAgent(fakeAi, new ObjectMapper());

        IntentResult result = agent.analyze(
                new WorkflowContext("s", "CUST-1001", null, "我只有1000元，最多可以买几件衣服"),
                "");

        assertThat(result.toolCalls()).noneMatch(call -> ToolNames.SEARCH_PRODUCTS.equals(call.tool()));
        assertThat(result.toolCalls()).isEmpty();
    }

    @Test
    void explicitPurchaseStillCreatesOrderToolCall() {
        AiClientPort fakeAi = request -> AiResponse.fallback("{} ");
        IntentAgent agent = new IntentAgent(fakeAi, new ObjectMapper());

        IntentResult result = agent.analyze(
                new WorkflowContext("s", "CUST-1001", null, "我要买一件黑色卫衣"),
                "");

        assertThat(result.toolCalls()).hasSize(1);
        assertThat(result.toolCalls().get(0).tool()).isEqualTo(ToolNames.CREATE_ORDER);
        assertThat(result.toolCalls().get(0).arguments())
                .containsEntry("productId", "CLOTH-HOODIE-004")
                .containsEntry("quantity", 1);
    }

    @Test
    void catalogPriceQuestionCreatesKnowledgeSearchToolCall() {
        AiClientPort fakeAi = request -> AiResponse.fallback("{} ");
        IntentAgent agent = new IntentAgent(fakeAi, new ObjectMapper());

        IntentResult result = agent.analyze(
                new WorkflowContext("s", "CUST-1001", null, "价格刚好699的衣服是什么"),
                "");

        assertThat(result.toolCalls()).hasSize(1);
        assertThat(result.toolCalls().get(0).tool()).isEqualTo(ToolNames.SEARCH_KNOWLEDGE);
        assertThat(result.toolCalls().get(0).arguments()).containsEntry("query", "价格刚好699的衣服是什么");
    }

    @Test
    void catalogBudgetQuestionCreatesKnowledgeSearchToolCall() {
        AiClientPort fakeAi = request -> AiResponse.fallback("{} ");
        IntentAgent agent = new IntentAgent(fakeAi, new ObjectMapper());

        IntentResult result = agent.analyze(
                new WorkflowContext("s", "CUST-1001", null, "我的衣服比699便宜的有哪几种，我预算有限"),
                "");

        assertThat(result.toolCalls()).hasSize(1);
        assertThat(result.toolCalls().get(0).tool()).isEqualTo(ToolNames.SEARCH_KNOWLEDGE);
        assertThat(result.toolCalls().get(0).arguments()).containsEntry("query", "我的衣服比699便宜的有哪几种，我预算有限");
    }

    @Test
    void cheapestCatalogQuestionCreatesKnowledgeSearchToolCall() {
        AiClientPort fakeAi = request -> AiResponse.fallback("{} ");
        IntentAgent agent = new IntentAgent(fakeAi, new ObjectMapper());

        IntentResult result = agent.analyze(
                new WorkflowContext("s", "CUST-1001", null, "价格最便宜的衣服是哪一件"),
                "");

        assertThat(result.toolCalls()).hasSize(1);
        assertThat(result.toolCalls().get(0).tool()).isEqualTo(ToolNames.SEARCH_KNOWLEDGE);
    }

    @Test
    void purchaseIntentOverridesModelKnowledgeSearchToolCall() {
        AiClientPort fakeAi = request -> AiResponse.fallback("""
                {"intent":"KNOWLEDGE_QA","title":"商品知识查询","priority":"MEDIUM","confidence":0.95,"tool_calls":[{"tool":"SEARCH_KNOWLEDGE","arguments":{"query":"我要买黑色连帽卫衣","limit":5}}]}
                """);
        IntentAgent agent = new IntentAgent(fakeAi, new ObjectMapper());

        IntentResult result = agent.analyze(
                new WorkflowContext("s", "CUST-1001", null, "我要买黑色连帽卫衣"),
                "");

        assertThat(result.toolCalls()).hasSize(1);
        assertThat(result.toolCalls().get(0).tool()).isEqualTo(ToolNames.CREATE_ORDER);
        assertThat(result.toolCalls().get(0).arguments())
                .containsEntry("productId", "CLOTH-HOODIE-004")
                .containsEntry("quantity", 1);
    }

    @Test
    void purchaseSynonymStillCreatesOrderToolCall() {
        AiClientPort fakeAi = request -> AiResponse.fallback("{} ");
        IntentAgent agent = new IntentAgent(fakeAi, new ObjectMapper());

        IntentResult result = agent.analyze(
                new WorkflowContext("s", "CUST-1001", null, "来一件黑色卫衣"),
                "");

        assertThat(result.toolCalls()).hasSize(1);
        assertThat(result.toolCalls().get(0).tool()).isEqualTo(ToolNames.CREATE_ORDER);
        assertThat(result.toolCalls().get(0).arguments())
                .containsEntry("productId", "CLOTH-HOODIE-004")
                .containsEntry("quantity", 1);
    }

    @Test
    void orderListQuestionStillCreatesQueryOrderToolCall() {
        AiClientPort fakeAi = request -> AiResponse.fallback("{} ");
        IntentAgent agent = new IntentAgent(fakeAi, new ObjectMapper());

        IntentResult result = agent.analyze(
                new WorkflowContext("s", "CUST-1001", null, "我有几个订单"),
                "");

        assertThat(result.toolCalls()).hasSize(1);
        assertThat(result.toolCalls().get(0).tool()).isEqualTo(ToolNames.QUERY_ORDER);
        assertThat(result.toolCalls().get(0).arguments()).containsEntry("customerId", "CUST-1001");
    }
}
