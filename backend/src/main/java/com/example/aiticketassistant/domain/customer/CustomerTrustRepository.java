package com.example.aiticketassistant.domain.customer;

import java.util.Optional;

public interface CustomerTrustRepository {
    Optional<CustomerAddress> findDefaultAddress(String customerId);
    Optional<CustomerAddress> findAddressForCustomer(String addressId, String customerId);
    Optional<CustomerPaymentMethod> findDefaultPaymentMethod(String customerId);
    Optional<CustomerPaymentMethod> findPaymentMethodForCustomer(String paymentMethodId, String customerId);
}
