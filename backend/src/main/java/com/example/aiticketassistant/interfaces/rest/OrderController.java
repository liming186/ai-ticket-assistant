package com.example.aiticketassistant.interfaces.rest;

import com.example.aiticketassistant.domain.order.Order;
import com.example.aiticketassistant.domain.order.OrderRepository;
import com.example.aiticketassistant.domain.shared.NotFoundException;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepository repository;

    public OrderController(OrderRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Map<String, Object> list(@RequestParam(defaultValue = "CUST-1001") String customerId) {
        return Map.of("orders", repository.findRecentByCustomerId(customerId, 20).stream()
                .map(order -> Map.of(
                        "orderNo", order.orderNo(),
                        "customerId", order.customerId(),
                        "amount", order.amount(),
                        "orderStatus", order.orderStatus(),
                        "paymentStatus", order.paymentStatus(),
                        "businessSummary", order.aiBusinessSummary()))
                .toList());
    }

    @GetMapping("/{orderNo}")
    public Map<String, Object> get(@PathVariable String orderNo) {
        Order order = repository.findByOrderNo(orderNo)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderNo));
        return Map.of(
                "orderNo", order.orderNo(),
                "customerId", order.customerId(),
                "amount", order.amount(),
                "orderStatus", order.orderStatus(),
                "paymentStatus", order.paymentStatus(),
                "businessSummary", order.aiBusinessSummary());
    }
}
