package com.seckinyener.ing.broker.controller;

import com.seckinyener.ing.broker.model.dto.*;
import com.seckinyener.ing.broker.service.ICustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final ICustomerService customerService;

    @PostMapping
    ResponseEntity<CustomerDetailsDto> createCustomer(@RequestBody CreateCustomerDto createCustomerDto) {
        return new ResponseEntity<>(customerService.createCustomer(createCustomerDto), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or @accessControlService.isCustomerAuthorizedByCustomerName(#customerId, authentication.name)")
    @GetMapping("/{customerId}/assets")
    ResponseEntity<List<AssetDetailsDto>> getCustomerAssets(@PathVariable("customerId") Long customerId) {
        return new ResponseEntity<>(customerService.getCustomerAssets(customerId), HttpStatus.OK);
    }

}
