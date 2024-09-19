package com.seckinyener.ing.broker.service;

import com.seckinyener.ing.broker.exception.OrderNotFoundException;
import com.seckinyener.ing.broker.exception.OrderStatusNotValidException;
import com.seckinyener.ing.broker.exception.TRYBalanceIsNotEnoughException;
import com.seckinyener.ing.broker.model.dto.CreateOrderDto;
import com.seckinyener.ing.broker.model.dto.OrderDetailsDto;
import com.seckinyener.ing.broker.model.dto.OrderFilterRequest;
import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.model.entity.Order;
import com.seckinyener.ing.broker.model.enumerated.SideEnum;
import com.seckinyener.ing.broker.model.enumerated.StatusEnum;
import com.seckinyener.ing.broker.repository.OrderRepository;
import com.seckinyener.ing.broker.service.impl.AssetService;
import com.seckinyener.ing.broker.service.impl.CustomerService;
import com.seckinyener.ing.broker.service.impl.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.seckinyener.ing.broker.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private CustomerService customerService;

    @Mock
    private AssetService assetService;

    @Mock
    private OrderRepository orderRepository;

    @Value("${try.currency}")
    private String currency;

    @Test
    void createOrderShouldThrowTRYBalanceIsNotEnoughException() {
        CreateOrderDto createOrderDto = new CreateOrderDto(customerId, assetName1, SideEnum.BUY, new BigDecimal(10), new BigDecimal(20));
        Customer customer = new Customer();
        Asset assetTRY = createAsset("TRY", BigDecimal.TEN, BigDecimal.TEN);

        when(customerService.findCustomerById(customerId)).thenReturn(customer);
        when(assetService.findAssetByCustomerIdAndName(customerId, currency)).thenReturn(assetTRY);

        TRYBalanceIsNotEnoughException thrown = assertThrows(TRYBalanceIsNotEnoughException.class, () -> {
            orderService.createOrder(createOrderDto);
        });

        assertEquals("TRY balance is not enough for this order.", thrown.getMessage());
    }

    @Test
    void createOrderShouldCreate() {
        CreateOrderDto createOrderDto = new CreateOrderDto(customerId, assetName1, SideEnum.BUY, new BigDecimal(10), new BigDecimal(20));
        Customer customer = new Customer();
        Asset assetTRY = createAsset("TRY", new BigDecimal(1000), new BigDecimal(1000));

        Order order = new Order();
        order.setAsset(assetName1);
        order.setSize(createOrderDto.size());
        order.setPrice(createOrderDto.price());
        order.setStatus(StatusEnum.PENDING);
        order.setOrderSide(SideEnum.BUY);
        order.setCreateDate(LocalDateTime.now());

        when(customerService.findCustomerById(customerId)).thenReturn(customer);
        when(assetService.findAssetByCustomerIdAndName(customerId, currency)).thenReturn(assetTRY);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDetailsDto result = orderService.createOrder(createOrderDto);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        verify(assetService).updateUsableSizeOfAssetBySubtractingAmount(eq(assetTRY), any(BigDecimal.class));

        Order capturedOrder = orderCaptor.getValue();
        assertEquals(createOrderDto.asset(), capturedOrder.getAsset());
        assertEquals(createOrderDto.size(), capturedOrder.getSize());
        assertEquals(createOrderDto.price(), capturedOrder.getPrice());
        assertEquals(StatusEnum.PENDING, capturedOrder.getStatus());
        assertEquals(SideEnum.BUY, capturedOrder.getOrderSide());

        assertEquals(createOrderDto.asset(), result.asset());
        assertEquals(createOrderDto.size(), result.size());
        assertEquals(createOrderDto.price(), result.price());
        assertEquals(StatusEnum.PENDING, result.status());
        assertEquals(SideEnum.BUY, result.side());
    }

    @Test
    void getOrderListOfUserForDateRangeWhenBothTheDatesAreEmpty() {
        List<Order> orders = createOrdersForListAPI();
        Customer customer = new Customer();
        customer.setOrders(orders);
        OrderFilterRequest orderFilterRequest = new OrderFilterRequest(customerId, null, null);

        when(customerService.findCustomerById(customerId)).thenReturn(customer);

        List<OrderDetailsDto> response = orderService.getOrderListOfUserForDateRange(orderFilterRequest);

        assertFalse(response.isEmpty());
        assertEquals(orders.get(2).getOrderSide(), response.get(0).side());
        assertEquals(orders.get(2).getAsset(), response.get(0).asset());
        assertEquals(orders.get(2).getCreateDate(), response.get(0).createDate());
    }

    @Test
    void getOrderListOfUserForDateRangeWhenStartDateIsEmpty() {
        List<Order> orders = createOrdersForListAPI();
        Customer customer = new Customer();
        String endDate = "2024-09-18T00:00";
        OrderFilterRequest orderFilterRequest = new OrderFilterRequest(customerId, null, endDate);

        List<Order> filteredList = orders.stream().filter(item -> item.getCreateDate().isBefore(LocalDateTime.parse(endDate))).toList();
        when(customerService.findCustomerById(customerId)).thenReturn(customer);
        when(orderRepository.findAllByCustomerIdAndCreateDateBefore(customerId, LocalDateTime.parse(endDate))).thenReturn(filteredList);


        List<OrderDetailsDto> response = orderService.getOrderListOfUserForDateRange(orderFilterRequest);

        assertFalse(response.isEmpty());
        assertEquals(filteredList.size(), response.size());
        assertEquals(filteredList.size(), 2);
        assertEquals(orders.get(1).getOrderSide(), response.get(0).side());
        assertEquals(orders.get(1).getAsset(), response.get(0).asset());
        assertEquals(orders.get(1).getCreateDate(), response.get(0).createDate());
    }

    @Test
    void getOrderListOfUserForDateRangeWhenEndDateIsEmpty() {
        List<Order> orders = createOrdersForListAPI();
        Customer customer = new Customer();
        String startDate = "2024-09-18T00:00";
        OrderFilterRequest orderFilterRequest = new OrderFilterRequest(customerId, startDate, null);

        List<Order> filteredList = orders.stream().filter(item -> item.getCreateDate().isAfter(LocalDateTime.parse(startDate))).toList();
        when(customerService.findCustomerById(customerId)).thenReturn(customer);
        when(orderRepository.findAllByCustomerIdAndCreateDateAfter(customerId, LocalDateTime.parse(startDate))).thenReturn(filteredList);


        List<OrderDetailsDto> response = orderService.getOrderListOfUserForDateRange(orderFilterRequest);

        assertFalse(response.isEmpty());
        assertEquals(filteredList.size(), response.size());
        assertEquals(filteredList.size(), 1);
        assertEquals(response.get(0).asset(), "ISB");
        verify(orderRepository, times(1)).findAllByCustomerIdAndCreateDateAfter(customerId, LocalDateTime.parse(startDate));
    }

    @Test
    void deleteOrderShouldThrowOrderNotFoundException() {
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.empty());
        OrderNotFoundException thrown = assertThrows(OrderNotFoundException.class, () -> {
            orderService.deleteOrder(orderId);
        });

        assertEquals("Order not found with id: " + orderId, thrown.getMessage());
    }

    @Test
    void deleteOrderShouldThrowOrderStatusNotValidException() {
        Order order = new Order();
        order.setStatus(StatusEnum.CANCELED);
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.of(order));
        OrderStatusNotValidException thrown = assertThrows(OrderStatusNotValidException.class, () -> {
            orderService.deleteOrder(orderId);
        });

        assertEquals("Order status not valid for deletion. Only Pending orders can be deleted..", thrown.getMessage());
    }

    @Test
    void deleteOrderShouldSuccessForBuyOrder() {
        Order order = createOrder(SideEnum.BUY, LocalDateTime.now(), assetName1, StatusEnum.PENDING);
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.of(order));


        OrderDetailsDto response = orderService.deleteOrder(orderId);

        assertEquals(StatusEnum.CANCELED, response.status());
        assertEquals(SideEnum.BUY, response.side());
        verify(assetService, times(1)).updateAssetUsableSizeWhenOrderIsDeleted(order);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void deleteOrderShouldSuccessForSellOrder() {
        Order order = createOrder(SideEnum.SELL, LocalDateTime.now(), assetName1, StatusEnum.PENDING);
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.of(order));


        OrderDetailsDto response = orderService.deleteOrder(orderId);

        assertEquals(StatusEnum.CANCELED, response.status());
        assertEquals(SideEnum.SELL, response.side());
        verify(assetService, times(0)).updateAssetUsableSizeWhenOrderIsDeleted(order);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void matchOrderShouldThrowOrderNotFoundException() {
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.empty());
        OrderNotFoundException thrown = assertThrows(OrderNotFoundException.class, () -> {
            orderService.matchOrder(orderId);
        });

        assertEquals("Order not found with id: " + orderId, thrown.getMessage());
    }

    @Test
    void matchOrderShouldThrowOrderStatusNotValidException() {
        Order order = new Order();
        order.setStatus(StatusEnum.CANCELED);
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.of(order));
        OrderStatusNotValidException thrown = assertThrows(OrderStatusNotValidException.class, () -> {
            orderService.matchOrder(orderId);
        });

        assertEquals("Order status not valid for matching. Only Pending orders can be matched..", thrown.getMessage());
    }

    @Test
    void matchOrderShouldSuccess() {
        Order order = createOrder(SideEnum.BUY, LocalDateTime.now(), assetName1, StatusEnum.PENDING);
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.of(order));

        OrderDetailsDto result = orderService.matchOrder(orderId);

        assertEquals(StatusEnum.MATCHED, result.status());
        assertEquals(SideEnum.BUY, result.side());
        verify(assetService, times(1)).updateAssetValuesForMatchedOrder(order);
        verify(orderRepository, times(1)).save(order);
    }
}
