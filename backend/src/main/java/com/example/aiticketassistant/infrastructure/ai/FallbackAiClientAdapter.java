package com.example.aiticketassistant.infrastructure.ai;

import com.example.aiticketassistant.application.agent.AiClientPort;
import com.example.aiticketassistant.application.agent.AiRequest;
import com.example.aiticketassistant.application.agent.AiResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "assistant.ai", name = "provider", havingValue = "fallback", matchIfMissing = true)
public class FallbackAiClientAdapter implements AiClientPort {
    private static final Pattern PRODUCT_ID_PATTERN = Pattern.compile("CLOTH-[A-Z]+-\\d{3}");

    @Override
    public AiResponse complete(AiRequest request) {
        String user = request.userPrompt() == null ? "" : request.userPrompt();
        String message = extractUserMessage(user);
        if (request.expectJson()) {
            if (isOrderListRequest(message)) {
                return AiResponse.fallback("""
                        {"intent":"QUERY_ORDER","title":"订单汇总查询","priority":"MEDIUM","confidence":0.58,"tool_calls":[{"tool":"QUERY_ORDER","arguments":{}}]}
                        """);
            }
            String productId = extractProductId(message);
            if (hasPurchaseIntent(message) && productId != null) {
                return AiResponse.fallback("""
                        {"intent":"GENERAL_SUPPORT","title":"安全下单确认","priority":"MEDIUM","confidence":0.6,"tool_calls":[{"tool":"CREATE_ORDER","arguments":{"productId":"%s","quantity":%d}}]}
                        """.formatted(productId, extractQuantity(message)));
            }
            if (hasPurchaseIntent(message)) {
                return AiResponse.fallback("""
                        {"intent":"GENERAL_SUPPORT","title":"需要选择商品","priority":"MEDIUM","confidence":0.5,"tool_calls":[]}
                        """);
            }
            if (message.contains("支付") && message.contains("工单")) {
                return AiResponse.fallback("""
                        {"intent":"CREATE_TICKET","title":"支付失败","priority":"HIGH","confidence":0.62,"tool_calls":[{"tool":"CREATE_TICKET","arguments":{"title":"支付失败","description":"用户反馈支付失败","priority":"HIGH"}}]}
                        """);
            }
            if (message.contains("订单") && (message.contains("怎么办") || message.contains("没生成"))) {
                return AiResponse.fallback("""
                        {"intent":"ORDER_AND_KNOWLEDGE","title":"支付成功但订单未生成","priority":"HIGH","confidence":0.61,"tool_calls":[{"tool":"QUERY_ORDER","arguments":{}},{"tool":"SEARCH_KNOWLEDGE","arguments":{"query":"支付成功但订单没生成怎么办","limit":5}}]}
                        """);
            }
            if (message.contains("订单") || message.contains("ORD-")) {
                return AiResponse.fallback("""
                        {"intent":"QUERY_ORDER","title":"订单状态查询","priority":"MEDIUM","confidence":0.58,"tool_calls":[{"tool":"QUERY_ORDER","arguments":{}}]}
                        """);
            }
            if (message.contains("退款") || message.contains("怎么") || message.contains("FAQ") || message.contains("知识")) {
                return AiResponse.fallback("""
                        {"intent":"KNOWLEDGE_QA","title":"知识库咨询","priority":"MEDIUM","confidence":0.55,"tool_calls":[{"tool":"SEARCH_KNOWLEDGE","arguments":{"limit":5}}]}
                        """);
            }
            return AiResponse.fallback("""
                    {"intent":"GENERAL_SUPPORT","title":"智能客服咨询","priority":"MEDIUM","confidence":0.5,"tool_calls":[]}
                    """);
        }
        if ("OrderAgent".equals(request.agentName())) {
            return AiResponse.fallback(orderFallback(user));
        }
        if ("KnowledgeAgent".equals(request.agentName())) {
            return AiResponse.fallback(knowledgeFallback(user));
        }
        if ("DirectQwen".equals(request.agentName())) {
            return AiResponse.fallback(directFallback(message));
        }
        return AiResponse.fallback("AI 服务未配置，当前使用本地规则兜底响应。");
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

    private int extractBudget(String message) {
        Matcher matcher = Pattern.compile("(\\d+)\\s*元?").matcher(message == null ? "" : message);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 500;
    }

    private boolean hasPurchaseIntent(String message) {
        return message.contains("买") || message.contains("购买") || message.contains("下单");
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

    private String extractUserMessage(String userPrompt) {
        String marker = "User message:";
        int start = userPrompt.indexOf(marker);
        if (start < 0) {
            return userPrompt;
        }
        int valueStart = start + marker.length();
        int end = userPrompt.indexOf('\n', valueStart);
        return (end < 0 ? userPrompt.substring(valueStart) : userPrompt.substring(valueStart, end)).trim();
    }

    private String directFallback(String message) {
        if (isMaxCountClothingQuestion(message)) {
            int budget = extractBudget(message);
            int unitPrice = 109;
            int quantity = budget / unitPrice;
            int total = quantity * unitPrice;
            int remaining = budget - total;
            return "%d元最多可以买%d件T恤，按每件%d元计算，合计%d元，剩余%d元。".formatted(budget, quantity, unitPrice, total, remaining);
        }
        if (message.contains("支付") && message.contains("订单") && (message.contains("没生成") || message.contains("怎么办"))) {
            return "建议先确认支付渠道是否扣款成功，保存支付凭证；如果订单仍未生成，联系商家客服核验支付流水，必要时申请补单或退款。";
        }
        if (message.contains("退款") && message.contains("怎么")) {
            return "一般先进入订单详情查看退款入口，提交退款原因和凭证后等待审核；如果没有对应订单或入口，需要联系商家客服处理。";
        }
        return "这是普通咨询，我可以直接回答；如果要查询你的订单、账户或执行下单/退款等业务动作，需要进入业务流程。";
    }

    private boolean isMaxCountClothingQuestion(String message) {
        String value = message == null ? "" : message;
        return (value.contains("衣服") || value.contains("T恤") || value.contains("服装"))
                && (value.contains("最多") || value.contains("几件") || value.contains("多少件"));
    }

    private String orderFallback(String userPrompt) {
        if (userPrompt.contains("totalOrders=") && userPrompt.contains("orders=")) {
            return orderListFallback(userPrompt);
        }
        if (userPrompt.contains("Order tool result: No order context found")) {
            return "未查询到订单上下文，无法给出订单状态判断。请提供订单号或先执行订单查询。";
        }
        if (userPrompt.contains("orderStatus=FAILED") || userPrompt.contains("订单生成失败")) {
            return "订单查询显示支付已完成但订单状态异常，应核验支付流水并进入补偿处理。";
        }
        if (userPrompt.contains("paymentStatus=PAID")) {
            return "订单已支付，请结合订单状态判断是否需要补单、退款或人工复核。";
        }
        return "已读取订单上下文，请按当前订单状态选择查询、补偿或人工处理。";
    }

    private String orderListFallback(String userPrompt) {
        Matcher count = Pattern.compile("totalOrders=(\\d+)").matcher(userPrompt);
        String total = count.find() ? count.group(1) : "多";
        StringBuilder answer = new StringBuilder("你当前一共有 ").append(total).append(" 个订单。\n");
        Matcher orderMatcher = Pattern.compile("orderNo=([^,}]+).*?amount=([0-9.]+).*?orderStatus=([A-Z_]+).*?paymentStatus=([A-Z_]+)").matcher(userPrompt);
        while (orderMatcher.find()) {
            answer.append("- ")
                    .append(orderMatcher.group(1).trim())
                    .append("：金额 ¥")
                    .append(orderMatcher.group(2).trim())
                    .append("，订单状态 ")
                    .append(orderMatcher.group(3).trim())
                    .append("，支付状态 ")
                    .append(orderMatcher.group(4).trim())
                    .append("\n");
        }
        return answer.toString().trim();
    }

    private String knowledgeFallback(String userPrompt) {
        if (userPrompt.contains("Retrieved sources:\n") && userPrompt.trim().endsWith("Retrieved sources:")) {
            return "未检索到直接匹配的知识库资料，建议转人工复核或补充问题细节。";
        }
        if (userPrompt.contains("退款")) {
            return "知识库建议：退款问题需先确认支付状态、退款渠道和预计到账时间。";
        }
        if (userPrompt.contains("支付")) {
            return "知识库建议：支付异常需保留支付凭证，核验支付流水后再判断补单或退款。";
        }
        return "知识库建议：根据检索资料处理该咨询；资料不足时请补充更多上下文。";
    }
}
