package com.example.aiticketassistant.infrastructure.repository;

import com.example.aiticketassistant.domain.customer.CustomerAddress;
import com.example.aiticketassistant.domain.customer.CustomerPaymentMethod;
import com.example.aiticketassistant.domain.customer.CustomerTrustRepository;
import com.example.aiticketassistant.infrastructure.persistence.jpa.CustomerAddressJpaEntity;
import com.example.aiticketassistant.infrastructure.persistence.jpa.CustomerPaymentMethodJpaEntity;
import com.example.aiticketassistant.infrastructure.persistence.jpa.SpringDataCustomerAddressJpaRepository;
import com.example.aiticketassistant.infrastructure.persistence.jpa.SpringDataCustomerPaymentMethodJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaCustomerTrustRepository implements CustomerTrustRepository {
    private final SpringDataCustomerAddressJpaRepository addressRepository;
    private final SpringDataCustomerPaymentMethodJpaRepository paymentMethodRepository;

    public JpaCustomerTrustRepository(SpringDataCustomerAddressJpaRepository addressRepository,
                                      SpringDataCustomerPaymentMethodJpaRepository paymentMethodRepository) {
        this.addressRepository = addressRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public Optional<CustomerAddress> findDefaultAddress(String customerId) {
        return addressRepository.findFirstByCustomerIdAndDefaultAddressTrueAndActiveTrue(customerId).map(this::toAddress);
    }

    @Override
    public Optional<CustomerAddress> findAddressForCustomer(String addressId, String customerId) {
        return addressRepository.findByIdAndCustomerIdAndActiveTrue(addressId, customerId).map(this::toAddress);
    }

    @Override
    public Optional<CustomerPaymentMethod> findDefaultPaymentMethod(String customerId) {
        return paymentMethodRepository.findFirstByCustomerIdAndDefaultMethodTrueAndActiveTrue(customerId).map(this::toPaymentMethod);
    }

    @Override
    public Optional<CustomerPaymentMethod> findPaymentMethodForCustomer(String paymentMethodId, String customerId) {
        return paymentMethodRepository.findByIdAndCustomerIdAndActiveTrue(paymentMethodId, customerId).map(this::toPaymentMethod);
    }

    private CustomerAddress toAddress(CustomerAddressJpaEntity entity) {
        return new CustomerAddress(entity.getId(), entity.getCustomerId(), entity.getRecipientName(), entity.getPhone(),
                entity.getAddressLine(), entity.isDefaultAddress(), entity.isActive());
    }

    private CustomerPaymentMethod toPaymentMethod(CustomerPaymentMethodJpaEntity entity) {
        return new CustomerPaymentMethod(entity.getId(), entity.getCustomerId(), entity.getMethodType(),
                entity.getDisplayLabel(), entity.isDefaultMethod(), entity.isActive());
    }
}
