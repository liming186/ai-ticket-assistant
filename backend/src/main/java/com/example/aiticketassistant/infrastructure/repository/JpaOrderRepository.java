package com.example.aiticketassistant.infrastructure.repository;

import com.example.aiticketassistant.domain.order.Order;
import com.example.aiticketassistant.domain.order.OrderId;
import com.example.aiticketassistant.domain.order.OrderRepository;
import com.example.aiticketassistant.domain.order.OrderStatus;
import com.example.aiticketassistant.domain.order.PaymentStatus;
import com.example.aiticketassistant.infrastructure.persistence.jpa.OrderJpaEntity;
import com.example.aiticketassistant.infrastructure.persistence.jpa.SpringDataOrderJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaOrderRepository implements OrderRepository {
    private final SpringDataOrderJpaRepository repository;

    public JpaOrderRepository(SpringDataOrderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Order> findByOrderNo(String orderNo) {
        return repository.findByOrderNo(orderNo).map(this::toDomain);
    }

    @Override
    public Optional<Order> findLatestByCustomerId(String customerId) {
        return repository.findTopByCustomerIdOrderByCreatedAtDesc(customerId).map(this::toDomain);
    }

    @Override
    public List<Order> findRecentByCustomerId(String customerId, int limit) {
        return repository.findTop20ByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .limit(Math.max(1, Math.min(20, limit)))
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Order save(Order order) {
        return toDomain(repository.save(toEntity(order)));
    }

    private Order toDomain(OrderJpaEntity entity) {
        return new Order(new OrderId(entity.getId()), entity.getOrderNo(), entity.getCustomerId(), entity.getAmount(),
                OrderStatus.valueOf(entity.getOrderStatus()), PaymentStatus.valueOf(entity.getPaymentStatus()),
                entity.getAddressId(), entity.getPaymentMethodId(), entity.getSource(), entity.getConfirmationId(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }

    private OrderJpaEntity toEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(order.id().value());
        entity.setOrderNo(order.orderNo());
        entity.setCustomerId(order.customerId());
        entity.setAmount(order.amount());
        entity.setOrderStatus(order.orderStatus().name());
        entity.setPaymentStatus(order.paymentStatus().name());
        entity.setAddressId(order.addressId());
        entity.setPaymentMethodId(order.paymentMethodId());
        entity.setSource(order.source());
        entity.setConfirmationId(order.confirmationId());
        entity.setCreatedAt(order.createdAt());
        entity.setUpdatedAt(order.updatedAt());
        return entity;
    }
}
