package com.seckinyener.ing.broker.service;

public interface IAccessControlService {

    boolean isCustomerAuthorizedByCustomerName(Long customerId, String customerName);

    boolean isCustomerAuthorizedByOrder(Long orderId, String customerName);
}
