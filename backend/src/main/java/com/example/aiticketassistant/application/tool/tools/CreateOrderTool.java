package com.example.aiticketassistant.application.tool.tools;

import com.example.aiticketassistant.application.order.CreateOrderConfirmationService;
import com.example.aiticketassistant.application.order.OrderConfirmationView;
import com.example.aiticketassistant.application.tool.ToolExecutor;
import com.example.aiticketassistant.application.tool.ToolMetadata;
import com.example.aiticketassistant.application.tool.ToolNames;
import com.example.aiticketassistant.application.tool.ToolResult;
import com.example.aiticketassistant.application.workflow.WorkflowContext;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderTool implements ToolExecutor {
    private final CreateOrderConfirmationService confirmationService;

    public CreateOrderTool(CreateOrderConfirmationService confirmationService) {
        this.confirmationService = confirmationService;
    }

    @Override
    public ToolMetadata metadata() {
        return new ToolMetadata(
                ToolNames.CREATE_ORDER,
                "Create a pending order confirmation from trusted catalog, stock, address, and payment method data. Never captures payment.",
                Map.of("productId", "trusted product id", "quantity", "integer", "addressId", "optional trusted address id", "paymentMethodId", "optional trusted payment method id"),
                Map.of("action", "CONFIRM_REQUIRED", "confirmationId", "string", "message", "string", "totalAmount", "number"));
    }

    @Override
    public ToolResult execute(Map<String, Object> arguments, WorkflowContext context) {
        String productId = string(arguments, "productId", null);
        int quantity = integer(arguments, "quantity", 1);
        String addressId = string(arguments, "addressId", null);
        String paymentMethodId = string(arguments, "paymentMethodId", null);
        OrderConfirmationView view = confirmationService.create(context, productId, quantity, addressId, paymentMethodId);
        context.remember("orderConfirmation", view);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("action", "CONFIRM_REQUIRED");
        data.put("confirmationId", view.confirmationId());
        data.put("sessionId", view.sessionId());
        data.put("customerId", view.customerId());
        data.put("message", view.message());
        data.put("productId", view.productId());
        data.put("productName", view.productName());
        data.put("quantity", view.quantity());
        data.put("unitPrice", view.unitPrice());
        data.put("totalAmount", view.totalAmount());
        data.put("address", view.address());
        data.put("paymentMethod", view.paymentMethod());
        data.put("expiresAt", view.expiresAt());
        return ToolResult.success(ToolNames.CREATE_ORDER, data);
    }

    private String string(Map<String, Object> args, String key, String fallback) {
        Object value = args == null ? null : args.get(key);
        return value == null || String.valueOf(value).isBlank() ? fallback : String.valueOf(value);
    }

    private int integer(Map<String, Object> args, String key, int fallback) {
        Object value = args == null ? null : args.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value != null) {
            try {
                return Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
        return fallback;
    }
}
