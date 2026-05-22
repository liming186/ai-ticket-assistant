package com.example.aiticketassistant.application.agent;

import com.example.aiticketassistant.application.tool.ToolCall;
import com.example.aiticketassistant.application.tool.ToolNames;
import com.example.aiticketassistant.application.workflow.WorkflowContext;
import com.example.aiticketassistant.domain.ticket.TicketPriority;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class IntentAgent {
    private static final Pattern PRODUCT_ID_PATTERN = Pattern.compile("CLOTH-[A-Z]+-\\d{3}");

    private final AiClientPort aiClient;
    private final ObjectMapper objectMapper;

    public IntentAgent(AiClientPort aiClient, ObjectMapper objectMapper) {
        this.aiClient = aiClient;
        this.objectMapper = objectMapper;
    }

    public IntentResult analyze(WorkflowContext context, String toolMetadata) {
        String systemPrompt = """
                You are Intent Agent in an enterprise support workflow.
                Return strict JSON only. Identify intent, extract parameters, and decide tool routing.
                Valid tools are provided by the platform; never invent a tool.
                Schema: {"intent":"CREATE_TICKET|QUERY_ORDER|UPDATE_TICKET|KNOWLEDGE_QA|ORDER_AND_KNOWLEDGE|GENERAL_SUPPORT","title":"string","priority":"LOW|MEDIUM|HIGH|URGENT","confidence":0.0,"tool_calls":[{"tool":"CREATE_TICKET|QUERY_ORDER|UPDATE_TICKET|SEARCH_KNOWLEDGE|CREATE_ORDER","arguments":{}}]}
                Use tools only for real business actions and customer/order data queries.
                Do not use tools for ordinary Q&A, budget arithmetic, product recommendations, product comparisons, or simple shopping advice.
                Use CREATE_ORDER only for explicit purchase intent with a trusted productId such as CLOTH-JEANS-003. Never invent price, address, or payment method.
                """;
        String userPrompt = """
                User message: %s
                CustomerId: %s
                OrderNo: %s
                Tool metadata: %s
                """.formatted(context.userMessage(), context.customerId(), context.orderNo(), toolMetadata);
        AiResponse response = aiClient.complete(new AiRequest(
                "IntentAgent",
                systemPrompt,
                userPrompt,
                Map.of("customerId", context.customerId(), "orderNo", context.orderNo() == null ? "" : context.orderNo()),
                true));
        return parseOrFallback(response.text(), context);
    }

    private IntentResult parseOrFallback(String json, WorkflowContext context) {
        try {
            JsonNode root = objectMapper.readTree(json);
            IntentType intent = IntentType.valueOf(root.path("intent").asText("GENERAL_SUPPORT"));
            TicketPriority priority = TicketPriority.normalize(root.path("priority").asText("MEDIUM"));
            String title = root.path("title").asText(defaultTitle(context.userMessage()));
            double confidence = root.path("confidence").asDouble(0.6);
            List<ToolCall> toolCalls = new ArrayList<>();
            JsonNode calls = root.path("tool_calls");
            if (calls.isArray()) {
                for (JsonNode call : calls) {
                    ToolCall normalized = normalizeToolCall(new ToolCall(
                            call.path("tool").asText(),
                            objectMapper.convertValue(call.path("arguments"), Map.class)), context.userMessage());
                    if (normalized != null) {
                        toolCalls.add(normalized);
                    }
                }
            }
            List<ToolCall> purchaseCalls = purchaseToolCalls(context.userMessage());
            if (!purchaseCalls.isEmpty()) {
                return new IntentResult(IntentType.GENERAL_SUPPORT, "安全下单确认", priority, Math.max(confidence, 0.9), purchaseCalls);
            }
            if (toolCalls.isEmpty()) {
                toolCalls = heuristicToolCalls(intent, title, priority, context);
            }
            return new IntentResult(intent, title, priority, confidence, toolCalls);
        } catch (Exception ignored) {
            IntentType intent = heuristicIntent(context.userMessage());
            TicketPriority priority = context.userMessage().contains("失败") || context.userMessage().contains("投诉")
                    ? TicketPriority.HIGH : TicketPriority.MEDIUM;
            String title = defaultTitle(context.userMessage());
            return new IntentResult(intent, title, priority, 0.55, heuristicToolCalls(intent, title, priority, context));
        }
    }

    private ToolCall normalizeToolCall(ToolCall call, String message) {
        if (ToolNames.SEARCH_PRODUCTS.equals(call.tool())) {
            return null;
        }
        return call;
    }

    private IntentType heuristicIntent(String message) {
        if (isOrderListRequest(message)) {
            return IntentType.QUERY_ORDER;
        }
        if (hasPurchaseIntent(message) && extractProductId(message) != null) {
            return IntentType.GENERAL_SUPPORT;
        }
        if (isCatalogFactQuestion(message)) {
            return IntentType.KNOWLEDGE_QA;
        }
        if (message.contains("创建") || message.contains("工单")) {
            return IntentType.CREATE_TICKET;
        }
        if (message.contains("订单") && (message.contains("怎么办") || message.contains("知识") || message.contains("没生成"))) {
            return IntentType.ORDER_AND_KNOWLEDGE;
        }
        if (message.contains("订单")) {
            return IntentType.QUERY_ORDER;
        }
        if (message.contains("怎么办") || message.contains("FAQ")) {
            return IntentType.KNOWLEDGE_QA;
        }
        return IntentType.GENERAL_SUPPORT;
    }

    private List<ToolCall> heuristicToolCalls(IntentType intent, String title, TicketPriority priority, WorkflowContext context) {
        List<ToolCall> calls = new ArrayList<>();
        if (isOrderListRequest(context.userMessage())) {
            calls.add(new ToolCall(ToolNames.QUERY_ORDER, Map.of("customerId", context.customerId())));
            return calls;
        }
        List<ToolCall> purchaseCalls = purchaseToolCalls(context.userMessage());
        if (!purchaseCalls.isEmpty()) {
            return purchaseCalls;
        }
        if (isCatalogFactQuestion(context.userMessage())) {
            calls.add(new ToolCall(ToolNames.SEARCH_KNOWLEDGE, Map.of(
                    "query", context.userMessage(),
                    "limit", 5)));
            return calls;
        }
        if (intent == IntentType.CREATE_TICKET) {
            calls.add(new ToolCall(ToolNames.CREATE_TICKET, Map.of(
                    "customerId", context.customerId(),
                    "title", title,
                    "description", context.userMessage(),
                    "priority", priority.name())));
        }
        if (intent == IntentType.QUERY_ORDER || intent == IntentType.ORDER_AND_KNOWLEDGE) {
            calls.add(new ToolCall(ToolNames.QUERY_ORDER, Map.of(
                    "customerId", context.customerId(),
                    "orderNo", context.orderNo() == null ? "" : context.orderNo())));
        }
        if (intent == IntentType.KNOWLEDGE_QA || intent == IntentType.ORDER_AND_KNOWLEDGE || context.userMessage().contains("怎么办")) {
            calls.add(new ToolCall(ToolNames.SEARCH_KNOWLEDGE, Map.of(
                    "query", context.userMessage(),
                    "limit", 5)));
        }
        return calls;
    }

    private boolean isOrderListRequest(String message) {
        String value = message == null ? "" : message;
        return value.contains("几个订单")
                || value.contains("多少订单")
                || value.contains("一共") && value.contains("订单")
                || value.contains("所有订单")
                || value.contains("全部订单")
                || value.contains("我的订单")
                || value.contains("买了") && value.contains("订单")
                || value.contains("展示") && value.contains("订单");
    }

    private List<ToolCall> purchaseToolCalls(String message) {
        String productId = extractProductId(message);
        if (!hasPurchaseIntent(message) || productId == null) {
            return List.of();
        }
        return List.of(new ToolCall(ToolNames.CREATE_ORDER, Map.of(
                "productId", productId,
                "quantity", extractQuantity(message))));
    }

    private boolean isCatalogFactQuestion(String message) {
        String value = message == null ? "" : message;
        return containsAny(value,
                "商品目录", "服装目录", "商品编号", "编号", "价格", "多少钱", "多少元", "刚好",
                "便宜", "最便宜", "最贵", "更便宜", "低于", "小于", "少于", "以内", "不超过", "预算", "预算有限", "哪几种", "哪些", "有什么")
                && containsAny(value, "衣服", "服装", "商品", "T恤", "衬衫", "牛仔裤", "卫衣", "连衣裙", "夹克", "半身裙", "Polo", "毛衣", "大衣");
    }

    private boolean hasPurchaseIntent(String message) {
        String value = message == null ? "" : message;
        return !isBudgetQuestion(value)
                && (containsAny(value,
                "买一件", "买一条", "买一个", "买一套", "买两件", "买2件", "买3件",
                "我要买", "我想买", "想买", "要买", "帮我买", "给我买",
                "购买", "立即购买", "下单", "帮我下单", "我要下单", "创建订单", "确认下单",
                "来一件", "来一条", "来一个", "来一套", "入手", "拿一件", "拿一条", "拿一个")
                || value.matches(".*(?:买|购买|下单)\\s*\\d+\\s*(?:件|个|条|套).*") );
    }

    private boolean isBudgetQuestion(String value) {
        return containsAny(value, "最多", "几件", "多少件", "买几件", "能买几件", "可以买几件", "预算", "搭配", "推荐", "建议", "比较", "怎么选");
    }

    private boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String extractProductId(String message) {
        String normalized = message == null ? "" : message;
        Matcher matcher = PRODUCT_ID_PATTERN.matcher(normalized);
        if (matcher.find()) {
            return matcher.group();
        }
        return productIdFromName(normalized);
    }

    private String productIdFromName(String message) {
        if (message.contains("白色T恤") || message.contains("纯棉T恤") || message.contains("T恤")) return "CLOTH-TEE-001";
        if (message.contains("牛津纺衬衫") || message.contains("蓝色衬衫") || message.contains("衬衫")) return "CLOTH-SHIRT-002";
        if (message.contains("水洗牛仔裤") || message.contains("直筒牛仔裤") || message.contains("牛仔裤")) return "CLOTH-JEANS-003";
        if (message.contains("连帽卫衣") || message.contains("黑色卫衣") || message.contains("卫衣")) return "CLOTH-HOODIE-004";
        if (message.contains("雪纺连衣裙") || message.contains("碎花连衣裙") || message.contains("连衣裙")) return "CLOTH-DRESS-005";
        if (message.contains("防风夹克") || message.contains("轻薄夹克") || message.contains("夹克")) return "CLOTH-JACKET-006";
        if (message.contains("A字半身裙") || message.contains("半身裙")) return "CLOTH-SKIRT-007";
        if (message.contains("Polo衫") || message.contains("POLO衫") || message.contains("polo衫")) return "CLOTH-POLO-008";
        if (message.contains("羊毛混纺大衣") || message.contains("羊毛大衣") || message.contains("大衣")) return "CLOTH-COAT-009";
        if (message.contains("针织毛衣") || message.contains("米色毛衣") || message.contains("毛衣")) return "CLOTH-SWEATER-010";
        return null;
    }

    private int extractQuantity(String message) {
        Matcher matcher = Pattern.compile("(?:买|购买|下单)\\s*(\\d+)\\s*(件|个|条|套)").matcher(message == null ? "" : message);
        if (matcher.find()) {
            return Math.max(1, Integer.parseInt(matcher.group(1)));
        }
        return 1;
    }

    private String defaultTitle(String message) {
        if (message.contains("支付")) {
            return "支付异常处理";
        }
        if (message.contains("订单")) {
            return "订单问题咨询";
        }
        return message.length() > 18 ? message.substring(0, 18) : message;
    }
}
