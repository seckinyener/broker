package com.seckinyener.ing.broker.controller;

import com.seckinyener.ing.broker.model.dto.AssetDetailsDto;
import com.seckinyener.ing.broker.model.dto.CreateCustomerDto;
import com.seckinyener.ing.broker.model.dto.CustomerDetailsDto;
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
    ResponseEntity<List<AssetDetailsDto>> getCustomer(@PathVariable("customerId") Long customerId) {
        return new ResponseEntity<>(customerService.getCustomerAssets(customerId), HttpStatus.OK);
    }

}
