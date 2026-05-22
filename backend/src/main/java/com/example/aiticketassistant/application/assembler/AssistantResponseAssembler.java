package com.example.aiticketassistant.application.assembler;

import com.example.aiticketassistant.application.workflow.WorkflowContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AssistantResponseAssembler {
    public String assemble(WorkflowContext context, String orderAnalysis, String knowledgeAnswer) {
        String tools = context.toolResults().stream()
                .map(result -> "- " + result.tool() + ": " + (result.success() ? "成功" : "失败 - " + result.error()))
                .collect(Collectors.joining("\n"));
        String ticket = context.recall("ticket") == null ? "未创建新工单" : "已创建/更新工单：" + context.recall("ticket");
        Object orderConfirmation = context.recall("orderConfirmation");
        Object productSearch = context.recall("productSearch");
        List<String> sections = new ArrayList<>();
        sections.add("我已完成 Multi-Agent 协同分析。");
        sections.add("**意图识别**：%s（置信度 %.2f）".formatted(
                context.intent() == null ? "GENERAL_SUPPORT" : context.intent().intent(),
                context.intent() == null ? 0.0 : context.intent().confidence()));
        if (orderAnalysis != null && !orderAnalysis.isBlank()) {
            sections.add("**订单分析**：\n" + orderAnalysis);
        }
        if (knowledgeAnswer != null && !knowledgeAnswer.isBlank()) {
            sections.add("**知识库结论**：\n" + knowledgeAnswer);
        }
        if (productSearch instanceof java.util.Map<?, ?> productData) {
            sections.add("**商品筛选**：\n" + formatProducts(productData));
        }
        if (orderConfirmation != null) {
            sections.add("**下单确认**：\n已生成待确认订单，请在页面确认商品、金额、地址和支付方式后再创建订单；系统不会自动扣款。");
        }
        sections.add("**业务动作**：\n" + ticket);
        sections.add("**工具执行 Trace**：\n" + (tools.isBlank() ? "- 未调用工具" : tools));
        return String.join("\n\n", sections) + "\n";
    }

    private String priceCondition(Object minPrice, Object maxPrice) {
        boolean hasMin = minPrice != null && !"不限".equals(String.valueOf(minPrice));
        boolean hasMax = maxPrice != null && !"不限".equals(String.valueOf(maxPrice));
        if (hasMin && hasMax) {
            return "大于 ¥" + minPrice + " 且小于 ¥" + maxPrice;
        }
        if (hasMin) {
            return "大于 ¥" + minPrice;
        }
        if (hasMax) {
            return "小于 ¥" + maxPrice;
        }
        return "价格不限";
    }

    private String formatProducts(java.util.Map<?, ?> productData) {
        Object products = productData.get("products");
        Object minPrice = productData.get("minPrice");
        Object maxPrice = productData.get("maxPrice");
        if (Boolean.parseBoolean(String.valueOf(productData.get("combinationMode")))) {
            return formatMaxAffordableProducts(productData);
        }
        StringBuilder text = new StringBuilder("符合条件（")
                .append(priceCondition(minPrice, maxPrice))
                .append("）的服装商品如下：\n\n");
        if (products instanceof java.util.List<?> list && !list.isEmpty()) {
            for (Object item : list) {
                if (item instanceof java.util.Map<?, ?> product) {
                    text.append("- ")
                            .append(product.get("name"))
                            .append("（")
                            .append(product.get("productId"))
                            .append("）：¥")
                            .append(product.get("price"))
                            .append("\n");
                }
            }
            text.append("\n你可以直接说：帮我买一件 商品名称。系统会再弹出确认，不会自动扣款。");
            return text.toString().trim();
        }
        return "没有找到符合预算的服装商品。";
    }

    private String formatMaxAffordableProducts(java.util.Map<?, ?> productData) {
        Object maxQuantity = productData.get("maxQuantity");
        if (maxQuantity == null || "0".equals(String.valueOf(maxQuantity))) {
            return "当前预算 ¥%s 无法购买任何服装商品。".formatted(productData.get("budget"));
        }
        return "按最便宜的商品计算，¥%s 最多可以买 %s 件%s（%s，单价 ¥%s），合计 ¥%s，剩余 ¥%s。\n\n你可以直接说：帮我买 %s 件 %s。系统会再弹出确认，不会自动扣款。".formatted(
                productData.get("budget"),
                productData.get("maxQuantity"),
                productData.get("bestProductName"),
                productData.get("bestProductId"),
                productData.get("bestProductPrice"),
                productData.get("totalPrice"),
                productData.get("remainingBudget"),
                productData.get("maxQuantity"),
                productData.get("bestProductName"));
    }
}
