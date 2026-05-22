package com.example.aiticketassistant.application.agent;

import com.example.aiticketassistant.application.tool.ToolResult;
import com.example.aiticketassistant.application.workflow.WorkflowContext;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class OrderAgent {
    private final AiClientPort aiClient;

    public OrderAgent(AiClientPort aiClient) {
        this.aiClient = aiClient;
    }

    public String analyze(WorkflowContext context) {
        Object orderData = context.toolResults().stream()
                .filter(result -> "QUERY_ORDER".equals(result.tool()))
                .findFirst()
                .map(ToolResult::data)
                .orElse(null);
        if (orderData instanceof Map<?, ?> map && map.get("orders") instanceof List<?> orders) {
            return orderListAnswer(map, orders);
        }
        String orderContext = String.valueOf(orderData == null ? "No order context found" : orderData);
        String system = """
                You are Order Agent. You make decisions from business context, but business commands are executed only by platform tools.
                Explain order/payment state, risk, and recommended next business action. Do not claim that a mutation happened unless a tool result proves it.
                """;
        String user = "User message: %s\nOrder tool result: %s".formatted(context.userMessage(), orderContext);
        return aiClient.complete(new AiRequest("OrderAgent", system, user, Map.of("orderContext", orderContext), false)).text();
    }

    private String orderListAnswer(Map<?, ?> data, List<?> orders) {
        Object totalOrders = data.get("totalOrders");
        StringBuilder answer = new StringBuilder("你当前一共有 ")
                .append(totalOrders == null ? orders.size() : totalOrders)
                .append(" 个订单。\n\n");
        for (Object item : orders) {
            if (item instanceof Map<?, ?> order) {
                answer.append("- ")
                        .append(itemNames(order))
                        .append("（")
                        .append(value(order, "orderNo"))
                        .append("）：金额 ¥")
                        .append(value(order, "amount"))
                        .append("，订单状态 ")
                        .append(value(order, "orderStatus"))
                        .append("，支付状态 ")
                        .append(value(order, "paymentStatus"))
                        .append("\n");
            }
        }
        return answer.toString().trim();
    }

    private String itemNames(Map<?, ?> map) {
        Object value = map.get("itemNames");
        if (value instanceof List<?> names && !names.isEmpty()) {
            return String.join("、", names.stream().map(String::valueOf).toList());
        }
        return "未记录商品明细";
    }

    private String value(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value == null ? "-" : String.valueOf(value);
    }
}
