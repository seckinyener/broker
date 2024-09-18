package com.seckinyener.ing.broker.controller;

import com.seckinyener.ing.broker.model.dto.DepositRequestDto;
import com.seckinyener.ing.broker.model.dto.DepositResponseDto;
import com.seckinyener.ing.broker.model.dto.WithdrawRequestDto;
import com.seckinyener.ing.broker.model.dto.WithdrawResponseDto;
import com.seckinyener.ing.broker.service.impl.AssetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asset")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PreAuthorize("hasRole('ADMIN') or @accessControlService.isCustomerAuthorizedByCustomerName(#customerId, authentication.name)")
    @PostMapping("/{customerId}/deposit")
    ResponseEntity<DepositResponseDto> depositMoneyForCustomer(@PathVariable("customerId") Long customerId, @RequestBody DepositRequestDto depositRequestDto) {
        return new ResponseEntity<>(assetService.depositMoneyForCustomer(customerId, depositRequestDto), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or @accessControlService.isCustomerAuthorizedByCustomerName(#customerId, authentication.name)")
    @PostMapping("/{customerId}/withdraw")
    ResponseEntity<WithdrawResponseDto> withdrawMoneyForCustomer(@PathVariable("customerId") Long customerId, @RequestBody WithdrawRequestDto withdrawRequestDto) {
        return new ResponseEntity<>(assetService.withdrawMoneyForCustomer(customerId, withdrawRequestDto), HttpStatus.OK);
    }
}
