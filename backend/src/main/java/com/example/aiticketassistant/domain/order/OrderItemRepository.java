package com.example.aiticketassistant.domain.order;

import java.util.List;

public interface OrderItemRepository {
    OrderItem save(OrderItem item);
    List<OrderItem> findByOrderId(String orderId);
}
