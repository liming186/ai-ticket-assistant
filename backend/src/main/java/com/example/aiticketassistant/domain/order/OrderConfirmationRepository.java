package com.example.aiticketassistant.domain.order;

import java.util.Optional;

public interface OrderConfirmationRepository {
    OrderConfirmation save(OrderConfirmation confirmation);
    Optional<OrderConfirmation> findById(String id);
}
