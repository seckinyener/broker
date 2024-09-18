package com.seckinyener.ing.broker.service.impl;

import com.seckinyener.ing.broker.exception.*;
import com.seckinyener.ing.broker.model.dto.CreateOrderDto;
import com.seckinyener.ing.broker.model.dto.OrderDetailsDto;
import com.seckinyener.ing.broker.model.dto.OrderFilterRequest;
import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.model.entity.Order;
import com.seckinyener.ing.broker.model.enumerated.SideEnum;
import com.seckinyener.ing.broker.model.enumerated.StatusEnum;
import com.seckinyener.ing.broker.repository.AssetRepository;
import com.seckinyener.ing.broker.repository.OrderRepository;
import com.seckinyener.ing.broker.service.IOrderService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Service
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;

    private final CustomerService customerService;

    private final AssetService assetService;

    @Transactional
    @Override
    public OrderDetailsDto createOrder(CreateOrderDto createOrderDto) {
        Customer customer = customerService.findCustomerById(createOrderDto.userId());
        Asset assetTRY = assetService.findAssetByCustomerIdAndName(createOrderDto.userId(), "TRY");
        BigDecimal totalAmountOfOrder = createOrderDto.size().multiply(createOrderDto.price());
        if (assetTRY.getUsableSize().compareTo(totalAmountOfOrder) >= 0) {
            Order order = createOrderRecord(createOrderDto, customer);

            if (SideEnum.BUY.equals(createOrderDto.side())) {
                assetService.updateUsableSizeOfAssetBySubtractingAmount(assetTRY, totalAmountOfOrder);
            }

            return new OrderDetailsDto(order.getAsset(), order.getSize(), order.getPrice(), order.getStatus(), order.getOrderSide(), order.getCreateDate());
        } else {
            throw new TRYBalanceIsNotEnoughException("TRY balance is not enough for this order.");
        }
    }

    private Order createOrderRecord(CreateOrderDto createOrderDto, Customer customer) {
        Order order = new Order();
        order.setOrderSide(createOrderDto.side());
        order.setCustomer(customer);
        order.setStatus(StatusEnum.PENDING);
        order.setSize(createOrderDto.size());
        order.setPrice(createOrderDto.price());
        order.setAsset(createOrderDto.asset());
        orderRepository.save(order);
        return order;
    }

    @Override
    public List<OrderDetailsDto> getOrderListOfUserForDateRange(OrderFilterRequest orderFilterRequest) {
        Customer customer = customerService.findCustomerById(orderFilterRequest.customerId());
        List<Order> orderList;
        if (Objects.isNull(orderFilterRequest.getStartDate()) && Objects.isNull(orderFilterRequest.getEndDate())) {
            orderList = customer.getOrders();
        } else if (Objects.isNull(orderFilterRequest.getStartDate())) {
            orderList = orderRepository.findAllByCustomerIdAndCreateDateBefore(orderFilterRequest.customerId(), orderFilterRequest.getEndDate());
        } else if (Objects.isNull(orderFilterRequest.getEndDate())) {
            orderList = orderRepository.findAllByCustomerIdAndCreateDateAfter(orderFilterRequest.customerId(), orderFilterRequest.getStartDate());
        } else {
            orderList = orderRepository.findAllByCustomerIdAndCreateDateBetween(orderFilterRequest.customerId(), orderFilterRequest.getStartDate(), orderFilterRequest.getEndDate());
        }

        return orderList.stream().sorted(Comparator.comparing(Order::getCreateDate).reversed()).map(item -> new OrderDetailsDto(item.getAsset(), item.getSize(), item.getPrice()
                , item.getStatus(), item.getOrderSide(), item.getCreateDate())).toList();
    }

    @Transactional
    @Override
    public OrderDetailsDto deleteOrder(Long orderId) {
        Order order = orderRepository.findOrderById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
        if (StatusEnum.PENDING.equals(order.getStatus())) {
            order.setStatus(StatusEnum.CANCELED);
            if (SideEnum.BUY.equals(order.getOrderSide())) {
                assetService.updateAssetUsableSizeWhenOrderIsDeleted(order);
            }
            orderRepository.save(order);
            return new OrderDetailsDto(order.getAsset(), order.getSize(), order.getPrice(), order.getStatus(), order.getOrderSide(), order.getCreateDate());
        } else {
            throw new OrderStatusNotValidException("Order status not valid for deletion. Only Pending orders can be deleted..");
        }
    }

    @Transactional
    @Override
    public OrderDetailsDto matchOrder(Long orderId) {
        Order order = orderRepository.findOrderById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
        if (StatusEnum.PENDING.equals(order.getStatus())) {
            assetService.updateAssetValuesForMatchedOrder(order);
            order.setStatus(StatusEnum.MATCHED);
            orderRepository.save(order);
            return new OrderDetailsDto(order.getAsset(), order.getSize(), order.getPrice(), order.getStatus(), order.getOrderSide(), order.getCreateDate());
        } else {
            throw new OrderStatusNotValidException("Order status not valid for matching. Only Pending orders can be matched..");
        }
    }
}
