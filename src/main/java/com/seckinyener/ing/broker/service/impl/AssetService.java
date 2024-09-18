package com.seckinyener.ing.broker.service.impl;

import com.seckinyener.ing.broker.exception.AssetNotFoundException;
import com.seckinyener.ing.broker.exception.UsableSizeIsNotSufficientForWithdrawException;
import com.seckinyener.ing.broker.model.dto.DepositRequestDto;
import com.seckinyener.ing.broker.model.dto.DepositResponseDto;
import com.seckinyener.ing.broker.model.dto.WithdrawRequestDto;
import com.seckinyener.ing.broker.model.dto.WithdrawResponseDto;
import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.model.entity.Order;
import com.seckinyener.ing.broker.model.enumerated.SideEnum;
import com.seckinyener.ing.broker.repository.AssetRepository;
import com.seckinyener.ing.broker.service.IAssetService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AssetService implements IAssetService {

    private final AssetRepository assetRepository;

    @Override
    public Asset findAssetByCustomerIdAndName(Long customerId, String name) {
        return assetRepository.findAssetByCustomerIdAndName(customerId, name).orElseThrow(() -> new AssetNotFoundException("Asset not found with customer id: " + customerId + " and " + name));
    }

    @Override
    public void createAssetForCustomer(BigDecimal amount, Customer customer) {
        Asset customerTRYAsset = new Asset();
        customerTRYAsset.setName("TRY");
        customerTRYAsset.setSize(amount);
        customerTRYAsset.setCustomer(customer);
        customerTRYAsset.setUsableSize(amount);
        assetRepository.save(customerTRYAsset);
    }

    @Override
    public void updateUsableSizeOfAssetBySubtractingAmount(Asset asset, BigDecimal amount) {
        asset.setUsableSize(asset.getUsableSize().subtract(amount));
        assetRepository.save(asset);
    }

    private Asset createAssetWithZeroSize(String assetName, Customer customer) {
        Asset asset = new Asset();
        asset.setName(assetName);
        asset.setCustomer(customer);
        asset.setSize(BigDecimal.ZERO);
        asset.setUsableSize(BigDecimal.ZERO);
        return asset;
    }

    private Asset getAssetOfOrder(Order order) {
        Optional<Asset> optionalCustomerAssetOfOrder = assetRepository.findAssetByCustomerIdAndName(order.getId(), order.getAsset());
        return optionalCustomerAssetOfOrder.orElseGet(() -> createAssetWithZeroSize(order.getAsset(), order.getCustomer()));
    }

    @Override
    public void updateAssetValuesForMatchedOrder(Order order) {
        Asset customerTRYBalance = findAssetByCustomerIdAndName(order.getCustomer().getId(), "TRY");
        Asset customerAssetOfOrder = getAssetOfOrder(order);

        if (SideEnum.SELL.equals(order.getOrderSide())) {
            customerTRYBalance.setSize(customerTRYBalance.getSize().add(order.getPrice().multiply(order.getSize())));
            customerTRYBalance.setUsableSize(customerTRYBalance.getUsableSize().add(order.getPrice().multiply(order.getSize())));
            customerAssetOfOrder.setUsableSize(customerAssetOfOrder.getUsableSize().subtract(order.getSize()));
        } else if (SideEnum.BUY.equals(order.getOrderSide())) {
            customerAssetOfOrder.setSize(customerAssetOfOrder.getSize().add(order.getSize()));
            customerAssetOfOrder.setUsableSize(customerAssetOfOrder.getUsableSize().add(order.getSize()));
        }

        assetRepository.save(customerAssetOfOrder);
        assetRepository.save(customerTRYBalance);
    }

    @Override
    public void updateAssetUsableSizeWhenOrderIsDeleted(Order order) {
        Asset assetTRY = assetRepository.findAssetByCustomerIdAndName(order.getCustomer().getId(), "TRY").orElseThrow(() -> new AssetNotFoundException("Asset not found with customer id: " + order.getCustomer().getId() + " and TRY"));
        BigDecimal orderAmount = order.getPrice().multiply(order.getSize());
        assetTRY.setUsableSize(assetTRY.getUsableSize().add(orderAmount));
        assetRepository.save(assetTRY);
    }

    @Transactional
    @Override
    public DepositResponseDto depositMoneyForCustomer(Long customerId, DepositRequestDto depositRequestDto) {
        Asset assetTRY = findAssetByCustomerIdAndName(customerId, "TRY");
        assetTRY.setUsableSize(assetTRY.getUsableSize().add(depositRequestDto.amount()));
        assetTRY.setSize(assetTRY.getSize().add(depositRequestDto.amount()));
        assetRepository.save(assetTRY);
        return new DepositResponseDto(assetTRY.getSize(), assetTRY.getUsableSize());
    }

    @Transactional
    @Override
    public WithdrawResponseDto withdrawMoneyForCustomer(Long customerId, WithdrawRequestDto withdrawRequestDto) {
        Asset assetTRY = findAssetByCustomerIdAndName(customerId, "TRY");
        if (assetTRY.getUsableSize().compareTo(withdrawRequestDto.amount()) < 0) {
            throw new UsableSizeIsNotSufficientForWithdrawException("Usable size is not sufficient to transfer this amount to iban");
        }
        // We can think to send this amount to the iban number which is provided in request dto.
        assetTRY.setSize(assetTRY.getSize().subtract(withdrawRequestDto.amount()));
        assetTRY.setUsableSize(assetTRY.getUsableSize().subtract(withdrawRequestDto.amount()));
        assetRepository.save(assetTRY);
        return new WithdrawResponseDto(assetTRY.getSize(), assetTRY.getUsableSize(), withdrawRequestDto.iban());
    }
}
