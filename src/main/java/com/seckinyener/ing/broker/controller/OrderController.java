package com.seckinyener.ing.broker.controller;

import com.seckinyener.ing.broker.model.dto.CreateOrderDto;
import com.seckinyener.ing.broker.model.dto.OrderDetailsDto;
import com.seckinyener.ing.broker.model.dto.OrderFilterRequest;
import com.seckinyener.ing.broker.service.IOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {

    private final IOrderService orderService;


    @PreAuthorize("hasRole('ROLE_ADMIN') or @accessControlService.isCustomerAuthorizedByCustomerName(#createOrderDto.userId(), authentication.name)")
    @PostMapping
    ResponseEntity<OrderDetailsDto> createOrder(@RequestBody CreateOrderDto createOrderDto) {
        return new ResponseEntity<>(orderService.createOrder(createOrderDto), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/list")
    ResponseEntity<List<OrderDetailsDto>> getOrderListOfUserForDateRange(@RequestBody OrderFilterRequest orderFilterRequest) {
        return new ResponseEntity<>(orderService.getOrderListOfUserForDateRange(orderFilterRequest), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or @accessControlService.isCustomerAuthorizedByOrder(#orderId, authentication.name)")
    @DeleteMapping("/delete/{orderId}")
    ResponseEntity<OrderDetailsDto> deleteOrder(@PathVariable(name="orderId") Long orderId){
        return new ResponseEntity<>(orderService.deleteOrder(orderId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/match/{orderId}")
    ResponseEntity<OrderDetailsDto> matchOrder(@PathVariable(name="orderId") Long orderId){
        return new ResponseEntity<>(orderService.matchOrder(orderId), HttpStatus.OK);
    }
}
