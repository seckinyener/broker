package com.seckinyener.ing.broker.service;

import com.seckinyener.ing.broker.exception.AssetNotFoundException;
import com.seckinyener.ing.broker.exception.UsableSizeIsNotSufficientForWithdrawException;
import com.seckinyener.ing.broker.model.dto.DepositRequestDto;
import com.seckinyener.ing.broker.model.dto.DepositResponseDto;
import com.seckinyener.ing.broker.model.dto.WithdrawRequestDto;
import com.seckinyener.ing.broker.model.dto.WithdrawResponseDto;
import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.model.entity.Order;
import com.seckinyener.ing.broker.model.enumerated.RoleEnum;
import com.seckinyener.ing.broker.model.enumerated.SideEnum;
import com.seckinyener.ing.broker.model.enumerated.StatusEnum;
import com.seckinyener.ing.broker.repository.AssetRepository;
import com.seckinyener.ing.broker.service.impl.AssetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static com.seckinyener.ing.broker.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @Spy
    @InjectMocks
    private AssetService assetService;

    @Mock
    private AssetRepository assetRepository;

    @Test
    void findAssetByCustomerIdAndNameShouldThrowAssetNotFoundException() {
        when(assetRepository.findAssetByCustomerIdAndName(customerId, assetName1)).thenReturn(Optional.empty());
        AssetNotFoundException exception = assertThrows(
                AssetNotFoundException.class,
                () -> assetService.findAssetByCustomerIdAndName(customerId, assetName1)
        );

        assertEquals("Asset not found with customer id: " + customerId + " and " + assetName1, exception.getMessage());
    }

    @Test
    void findAssetByCustomerIdAndNameShouldReturnAsset() {
        Asset asset = createAsset(assetName1, BigDecimal.TEN, BigDecimal.ONE);
        when(assetRepository.findAssetByCustomerIdAndName(customerId, assetName1)).thenReturn(Optional.of(asset));

        Asset response = assetService.findAssetByCustomerIdAndName(customerId, assetName1);
        assertEquals(response.getName(), asset.getName());
    }

    @Test
    void createAssetForCustomerShouldCreateAsset() {
        BigDecimal amount = new BigDecimal("1000");
        Customer customer = createCustomer(customerId, username, password, RoleEnum.ADMIN);

        assetService.createAssetForCustomer(amount, customer);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepository, times(1)).save(assetCaptor.capture());

        Asset capturedAsset = assetCaptor.getValue();
        assertEquals(capturedAsset.getName(), "TRY");
        assertEquals(capturedAsset.getUsableSize(), amount);
        assertEquals(capturedAsset.getSize(), amount);
        assertEquals(capturedAsset.getCustomer(), customer);
    }

    @Test
    void updateAssetValuesForMatchedOrderShouldRunForBuy() {
        Customer customer = createCustomer(customerId, username, password, RoleEnum.ADMIN);

        Order order = new Order();
        order.setAsset(assetName1);
        order.setOrderSide(SideEnum.BUY);
        order.setStatus(StatusEnum.PENDING);
        order.setPrice(BigDecimal.TEN);
        order.setSize(new BigDecimal(50));
        order.setCustomer(customer);
        order.setId(orderId);

        Asset customerTRYBalance = createAsset("TRY", new BigDecimal(1000), new BigDecimal(1000));
        Asset customerAssetOfOrder = createAsset(assetName1, BigDecimal.TEN, BigDecimal.TEN);

        doReturn(customerTRYBalance).when(assetService).findAssetByCustomerIdAndName(customerId, "TRY");
        when(assetRepository.findAssetByCustomerIdAndName(customerId, assetName1)).thenReturn(Optional.of(customerAssetOfOrder));

        assetService.updateAssetValuesForMatchedOrder(order);

        verify(assetRepository, times(1)).save(customerTRYBalance);
        verify(assetRepository, times(1)).save(customerAssetOfOrder);

        assertEquals(customerTRYBalance.getSize(), new BigDecimal(500));
    }

    @Test
    void updateAssetValuesForMatchedOrderShouldRunForSell() {
        Customer customer = createCustomer(customerId, username, password, RoleEnum.ADMIN);

        Order order = new Order();
        order.setAsset(assetName1);
        order.setOrderSide(SideEnum.SELL);
        order.setStatus(StatusEnum.PENDING);
        order.setPrice(BigDecimal.TEN);
        order.setSize(new BigDecimal(50));
        order.setCustomer(customer);
        order.setId(orderId);

        Asset customerTRYBalance = createAsset("TRY", new BigDecimal(1000), new BigDecimal(1000));
        Asset customerAssetOfOrder = createAsset(assetName1, BigDecimal.TEN, BigDecimal.TEN);

        doReturn(customerTRYBalance).when(assetService).findAssetByCustomerIdAndName(customerId, "TRY");
        when(assetRepository.findAssetByCustomerIdAndName(customerId, assetName1)).thenReturn(Optional.of(customerAssetOfOrder));

        assetService.updateAssetValuesForMatchedOrder(order);

        verify(assetRepository, times(1)).save(customerTRYBalance);
        verify(assetRepository, times(1)).save(customerAssetOfOrder);

        assertEquals(customerTRYBalance.getSize(), new BigDecimal(1500));
    }

    @Test
    void withdrawMoneyForCustomerShouldThrowSizeIsNotSufficientException() {
        Asset customerTRYBalance = createAsset("TRY", new BigDecimal(1000), new BigDecimal(1000));
        WithdrawRequestDto withdrawRequestDto = new WithdrawRequestDto(new BigDecimal(1500), "ibanNumber");

        doReturn(customerTRYBalance).when(assetService).findAssetByCustomerIdAndName(customerId, "TRY");

        UsableSizeIsNotSufficientForWithdrawException exception = assertThrows(
                UsableSizeIsNotSufficientForWithdrawException.class,
                () -> assetService.withdrawMoneyForCustomer(customerId, withdrawRequestDto)
        );

        assertEquals("Usable size is not sufficient to transfer this amount to iban", exception.getMessage());
    }

    @Test
    void withdrawMoneyForCustomerShouldSuccessAndReturnDto() {
        Asset customerTRYBalance = createAsset("TRY", new BigDecimal(1000), new BigDecimal(1000));
        WithdrawRequestDto withdrawRequestDto = new WithdrawRequestDto(new BigDecimal(500), "ibanNumber");

        doReturn(customerTRYBalance).when(assetService).findAssetByCustomerIdAndName(customerId, "TRY");

        WithdrawResponseDto response = assetService.withdrawMoneyForCustomer(customerId, withdrawRequestDto);

        assertEquals(response.newBalance(), new BigDecimal(500));
        assertEquals(response.usableBalance(), new BigDecimal(500));
        verify(assetRepository, times(1)).save(customerTRYBalance);
    }

    @Test
    void depositMoneyForCustomerShouldReturnDepositResponseDto() {
        Asset customerTRYBalance = createAsset("TRY", new BigDecimal(1000), new BigDecimal(1000));
        DepositRequestDto depositRequestDto = new DepositRequestDto(new BigDecimal(500));

        doReturn(customerTRYBalance).when(assetService).findAssetByCustomerIdAndName(customerId, "TRY");

        DepositResponseDto response = assetService.depositMoneyForCustomer(customerId, depositRequestDto);

        assertEquals(response.newBalance(), new BigDecimal(1500));
        assertEquals(response.newUsableBalance(), new BigDecimal(1500));
    }

    @Test
    void updateAssetUsableSizeWhenOrderIsDeletedShouldSave() {
        Customer customer = createCustomer(customerId, username, password, RoleEnum.ADMIN);

        Order order = new Order();
        order.setAsset(assetName1);
        order.setOrderSide(SideEnum.SELL);
        order.setStatus(StatusEnum.PENDING);
        order.setPrice(BigDecimal.TEN);
        order.setSize(new BigDecimal(50));
        order.setCustomer(customer);
        order.setId(orderId);

        Asset customerTRYBalance = createAsset("TRY", new BigDecimal(1000), new BigDecimal(1000));
        doReturn(customerTRYBalance).when(assetService).findAssetByCustomerIdAndName(customerId, "TRY");

        assetService.updateAssetUsableSizeWhenOrderIsDeleted(order);

        verify(assetRepository, times(1)).save(customerTRYBalance);
        assertEquals(customerTRYBalance.getUsableSize(), new BigDecimal(1500));
    }

    @Test
    void updateUsableSizeOfAssetBySubtractingAmountShouldSave() {
        Asset customerTRYBalance = createAsset("TRY", new BigDecimal(1000), new BigDecimal(1000));
        assetService.updateUsableSizeOfAssetBySubtractingAmount(customerTRYBalance, new BigDecimal(500));

        verify(assetRepository, times(1)).save(customerTRYBalance);
        assertEquals(customerTRYBalance.getUsableSize(), new BigDecimal(500));
    }

}
