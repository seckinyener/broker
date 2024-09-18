package com.seckinyener.ing.broker.service;

import com.seckinyener.ing.broker.exception.CustomerNotFoundException;
import com.seckinyener.ing.broker.exception.OrderNotFoundException;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.model.entity.Order;
import com.seckinyener.ing.broker.model.enumerated.RoleEnum;
import com.seckinyener.ing.broker.model.enumerated.SideEnum;
import com.seckinyener.ing.broker.model.enumerated.StatusEnum;
import com.seckinyener.ing.broker.repository.CustomerRepository;
import com.seckinyener.ing.broker.repository.OrderRepository;
import com.seckinyener.ing.broker.service.impl.AccessControlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.seckinyener.ing.broker.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccessControlServiceTest {

    @InjectMocks
    private AccessControlService accessControlService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void isCustomerAuthorizedByCustomerNameShouldReturnTrue() {
        Customer customer = createCustomer(customerId, username, password, RoleEnum.CUSTOMER);
        when(customerRepository.findCustomerByUsername(username)).thenReturn(Optional.of(customer));

        boolean result = accessControlService.isCustomerAuthorizedByCustomerName(customerId, username);
        assertTrue(result);
    }

    @Test
    void isCustomerAuthorizedByCustomerNameShouldReturnFalse() {
        Customer customer = createCustomer(customerId, username, password, RoleEnum.CUSTOMER);
        when(customerRepository.findCustomerByUsername(username)).thenReturn(Optional.of(customer));

        boolean result = accessControlService.isCustomerAuthorizedByCustomerName(customerId_2, username);
        assertFalse(result);
    }

    @Test
    void isCustomerAuthorizedByCustomerNameShouldThrowException() {
        when(customerRepository.findCustomerByUsername(username)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> accessControlService.isCustomerAuthorizedByCustomerName(customerId, username)
        );

        assertEquals("Customer not found with username: " + username, exception.getMessage());
    }

    @Test
    void isCustomerAuthorizedByOrderShouldReturnTrue() {
        Customer customer = new Customer();
        customer.setUsername(username);
        Order order = createOrder(SideEnum.BUY, LocalDateTime.now(), assetName1, StatusEnum.PENDING);
        order.setCustomer(customer);
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.of(order));
        boolean result = accessControlService.isCustomerAuthorizedByOrder(orderId, username);
        assertTrue(result);
    }

    @Test
    void isCustomerAuthorizedByOrderShouldReturnFalse() {
        Customer customer = new Customer();
        customer.setUsername(username);
        Order order = createOrder(SideEnum.BUY, LocalDateTime.now(), assetName1, StatusEnum.PENDING);
        order.setCustomer(customer);
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.of(order));
        boolean result = accessControlService.isCustomerAuthorizedByOrder(orderId, username_2);
        assertFalse(result);
    }

    @Test
    void isCustomerAuthorizedByOrderShouldThrowException() {
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> accessControlService.isCustomerAuthorizedByOrder(orderId, username_2)
        );

        assertEquals("Order not found with id: " + orderId, exception.getMessage());
    }
}
