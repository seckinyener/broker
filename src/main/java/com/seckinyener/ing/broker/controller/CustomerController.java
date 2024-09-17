package com.seckinyener.ing.broker.controller;

import com.seckinyener.ing.broker.model.dto.*;
import com.seckinyener.ing.broker.service.ICustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final ICustomerService customerService;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    ResponseEntity<CustomerDetailsDto> createCustomer(@RequestBody CreateCustomerDto createCustomerDto) {
        return new ResponseEntity<>(customerService.createCustomer(createCustomerDto), HttpStatus.OK);
    }

    @GetMapping("/{customerId}/assets")
    ResponseEntity<List<AssetDetailsDto>> getCustomerAssets(@PathVariable("customerId") Long customerId) {
        return new ResponseEntity<>(customerService.getCustomerAssets(customerId), HttpStatus.OK);
    }

    @PostMapping("/{customerId}/deposit")
    ResponseEntity<DepositResponseDto> depositMoneyForCustomer(@PathVariable("customerId") Long customerId, @RequestBody DepositRequestDto depositRequestDto) {
        return new ResponseEntity<>(customerService.depositMoneyForCustomer(customerId, depositRequestDto), HttpStatus.OK);
    }

}
