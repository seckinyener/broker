package com.seckinyener.ing.broker.controller;

import com.seckinyener.ing.broker.model.dto.*;
import com.seckinyener.ing.broker.service.ICustomerService;
import com.seckinyener.ing.broker.service.impl.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final ICustomerService customerService;

    private final AccessControlService accessControlService;

    public CustomerController(ICustomerService customerService, AccessControlService accessControlService) {
        this.customerService = customerService;
        this.accessControlService = accessControlService;
    }

    @PostMapping
    ResponseEntity<CustomerDetailsDto> createCustomer(@RequestBody CreateCustomerDto createCustomerDto) {
        return new ResponseEntity<>(customerService.createCustomer(createCustomerDto), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or @accessControlService.isCustomerAuthorizedByCustomerName(#customerId, authentication.name)")
    @GetMapping("/{customerId}/assets")
    ResponseEntity<List<AssetDetailsDto>> getCustomerAssets(@PathVariable("customerId") Long customerId) {
        return new ResponseEntity<>(customerService.getCustomerAssets(customerId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or @accessControlService.isCustomerAuthorizedByCustomerName(#customerId, authentication.name)")
    @PostMapping("/{customerId}/deposit")
    ResponseEntity<DepositResponseDto> depositMoneyForCustomer(@PathVariable("customerId") Long customerId, @RequestBody DepositRequestDto depositRequestDto) {
        return new ResponseEntity<>(customerService.depositMoneyForCustomer(customerId, depositRequestDto), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or @accessControlService.isCustomerAuthorizedByCustomerName(#customerId, authentication.name)")
    @PostMapping("/{customerId}/withdraw")
    ResponseEntity<WithdrawResponseDto> depositMoneyForCustomer(@PathVariable("customerId") Long customerId, @RequestBody WithdrawRequestDto withdrawRequestDto) {
        return new ResponseEntity<>(customerService.withdrawMoneyForCustomer(customerId, withdrawRequestDto), HttpStatus.OK);
    }

}
