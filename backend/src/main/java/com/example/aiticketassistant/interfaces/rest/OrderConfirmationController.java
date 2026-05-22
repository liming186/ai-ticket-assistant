package com.example.aiticketassistant.interfaces.rest;

import com.example.aiticketassistant.application.order.ConfirmOrderRequest;
import com.example.aiticketassistant.application.order.ConfirmOrderService;
import com.example.aiticketassistant.application.order.OrderCreationResult;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assistant/order-confirmations")
public class OrderConfirmationController {
    private final ConfirmOrderService confirmOrderService;

    public OrderConfirmationController(ConfirmOrderService confirmOrderService) {
        this.confirmOrderService = confirmOrderService;
    }

    @PostMapping("/{id}/confirm")
    public OrderCreationResult confirm(@PathVariable String id, @RequestBody ConfirmOrderRequest request) {
        return confirmOrderService.confirm(id, request);
    }

    @PostMapping("/{id}/cancel")
    public Map<String, Object> cancel(@PathVariable String id, @RequestBody ConfirmOrderRequest request) {
        return confirmOrderService.cancel(id, request);
    }
}
