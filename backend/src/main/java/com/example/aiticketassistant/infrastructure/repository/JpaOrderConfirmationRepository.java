package com.example.aiticketassistant.infrastructure.repository;

import com.example.aiticketassistant.domain.order.OrderConfirmation;
import com.example.aiticketassistant.domain.order.OrderConfirmationRepository;
import com.example.aiticketassistant.domain.order.OrderConfirmationStatus;
import com.example.aiticketassistant.infrastructure.persistence.jpa.OrderConfirmationJpaEntity;
import com.example.aiticketassistant.infrastructure.persistence.jpa.SpringDataOrderConfirmationJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaOrderConfirmationRepository implements OrderConfirmationRepository {
    private final SpringDataOrderConfirmationJpaRepository repository;

    public JpaOrderConfirmationRepository(SpringDataOrderConfirmationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrderConfirmation save(OrderConfirmation confirmation) {
        return toDomain(repository.save(toEntity(confirmation)));
    }

    @Override
    public Optional<OrderConfirmation> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    private OrderConfirmation toDomain(OrderConfirmationJpaEntity entity) {
        return new OrderConfirmation(
                entity.getId(),
                entity.getTraceId(),
                entity.getSessionId(),
                entity.getCustomerId(),
                entity.getProductId(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getTotalAmount(),
                entity.getAddressId(),
                entity.getPaymentMethodId(),
                OrderConfirmationStatus.valueOf(entity.getStatus()),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private OrderConfirmationJpaEntity toEntity(OrderConfirmation confirmation) {
        OrderConfirmationJpaEntity entity = new OrderConfirmationJpaEntity();
        entity.setId(confirmation.id());
        entity.setTraceId(confirmation.traceId());
        entity.setSessionId(confirmation.sessionId());
        entity.setCustomerId(confirmation.customerId());
        entity.setProductId(confirmation.productId());
        entity.setQuantity(confirmation.quantity());
        entity.setUnitPrice(confirmation.unitPrice());
        entity.setTotalAmount(confirmation.totalAmount());
        entity.setAddressId(confirmation.addressId());
        entity.setPaymentMethodId(confirmation.paymentMethodId());
        entity.setStatus(confirmation.status().name());
        entity.setExpiresAt(confirmation.expiresAt());
        entity.setCreatedAt(confirmation.createdAt());
        entity.setUpdatedAt(confirmation.updatedAt());
        return entity;
    }
}
