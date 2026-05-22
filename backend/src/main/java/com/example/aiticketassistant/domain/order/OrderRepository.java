package com.example.aiticketassistant.domain.order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findByOrderNo(String orderNo);
    Optional<Order> findLatestByCustomerId(String customerId);
    List<Order> findRecentByCustomerId(String customerId, int limit);
    Order save(Order order);
}
