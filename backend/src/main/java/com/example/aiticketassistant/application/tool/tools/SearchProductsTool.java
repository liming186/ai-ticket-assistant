package com.example.aiticketassistant.application.tool.tools;

import com.example.aiticketassistant.application.tool.ToolExecutor;
import com.example.aiticketassistant.application.tool.ToolMetadata;
import com.example.aiticketassistant.application.tool.ToolNames;
import com.example.aiticketassistant.application.tool.ToolResult;
import com.example.aiticketassistant.application.workflow.WorkflowContext;
import com.example.aiticketassistant.domain.catalog.Product;
import com.example.aiticketassistant.domain.catalog.ProductRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SearchProductsTool implements ToolExecutor {
    private final ProductRepository productRepository;

    public SearchProductsTool(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ToolMetadata metadata() {
        return new ToolMetadata(
                ToolNames.SEARCH_PRODUCTS,
                "Search trusted clothing product catalog by budget. Returns product IDs, names, and trusted prices.",
                Map.of("minPrice", "number", "maxPrice", "number", "category", "clothing"),
                Map.of("products", "array", "totalProducts", "integer"));
    }

    @Override
    public ToolResult execute(Map<String, Object> arguments, WorkflowContext context) {
        BigDecimal minPrice = decimal(arguments == null ? null : arguments.get("minPrice"));
        BigDecimal maxPrice = decimal(arguments == null ? null : arguments.get("maxPrice"));
        BigDecimal budget = decimal(arguments == null ? null : arguments.get("budget"));
        boolean combinationMode = bool(arguments == null ? null : arguments.get("combinationMode"));
        var activeProducts = productRepository.findActiveProducts();
        var products = activeProducts.stream()
                .filter(product -> minPrice == null || product.price().compareTo(minPrice) > 0)
                .filter(product -> maxPrice == null || product.price().compareTo(maxPrice) < 0)
                .map(product -> Map.<String, Object>of(
                        "productId", product.id(),
                        "name", product.name(),
                        "price", product.price()))
                .toList();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("category", "服装");
        data.put("minPrice", minPrice == null ? "不限" : minPrice);
        data.put("maxPrice", maxPrice == null ? "不限" : maxPrice);
        data.put("totalProducts", products.size());
        data.put("products", products);
        if (combinationMode && budget != null) {
            data.putAll(maxAffordableSummary(activeProducts, budget));
        }
        context.remember("productSearch", data);
        return ToolResult.success(ToolNames.SEARCH_PRODUCTS, data);
    }

    private Map<String, Object> maxAffordableSummary(java.util.List<Product> activeProducts, BigDecimal budget) {
        return activeProducts.stream()
                .min(Comparator.comparing(Product::price))
                .map(product -> {
                    int maxQuantity = budget.divide(product.price(), 0, RoundingMode.DOWN).intValue();
                    BigDecimal totalPrice = product.price().multiply(BigDecimal.valueOf(maxQuantity));
                    BigDecimal remainingBudget = budget.subtract(totalPrice);
                    return Map.<String, Object>of(
                            "combinationMode", true,
                            "budget", budget,
                            "maxQuantity", maxQuantity,
                            "bestProductId", product.id(),
                            "bestProductName", product.name(),
                            "bestProductPrice", product.price(),
                            "totalPrice", totalPrice,
                            "remainingBudget", remainingBudget);
                })
                .orElseGet(() -> Map.of("combinationMode", true, "budget", budget, "maxQuantity", 0));
    }

    private BigDecimal decimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        try {
            return new BigDecimal(String.valueOf(value).replace("元", "").trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private boolean bool(Object value) {
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }
}
