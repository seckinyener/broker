package com.seckinyener.ing.broker.service;

import com.seckinyener.ing.broker.exception.CustomerAlreadyExistException;
import com.seckinyener.ing.broker.exception.CustomerNotFoundException;
import com.seckinyener.ing.broker.model.dto.AssetDetailsDto;
import com.seckinyener.ing.broker.model.dto.CreateCustomerDto;
import com.seckinyener.ing.broker.model.dto.CustomerDetailsDto;
import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.model.enumerated.RoleEnum;
import com.seckinyener.ing.broker.repository.CustomerRepository;
import com.seckinyener.ing.broker.service.impl.AssetService;
import com.seckinyener.ing.broker.service.impl.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.seckinyener.ing.broker.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Spy
    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AssetService assetService;

    @Test
    void findCustomerByIdShouldThrowCustomerNotFoundException() {
        when(customerRepository.findCustomerById(customerId)).thenReturn(Optional.empty());
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.findCustomerById(customerId)
        );


        assertEquals("Customer not found with id: " + customerId, exception.getMessage());
        verify(customerRepository, times(1)).findCustomerById(customerId);
    }

    @Test
    void findCustomerByIdShouldReturnCustomer() {
        Customer customer = createCustomer(customerId, username, password, adminRole);
        when(customerRepository.findCustomerById(customerId)).thenReturn(Optional.of(customer));
        Customer response = customerService.findCustomerById(customerId);

        verify(customerRepository, times(1)).findCustomerById(customerId);
        assertEquals(response.getId(), customerId);
        assertEquals(response.getUsername(), username);
        assertEquals(response.getPassword(), password);
        assertEquals(response.getRole(), adminRole);
    }

    @Test
    void getCustomerAssetsShouldReturnList() {
        Asset asset1 = createAsset(assetName1, BigDecimal.ONE, BigDecimal.ONE);
        Asset asset2 = createAsset(assetName2, BigDecimal.TEN, BigDecimal.TEN);
        Customer customer = createCustomer(customerId, username, password, adminRole);
        customer.setAssets(List.of(asset1, asset2));

        doReturn(customer).when(customerService).findCustomerById(customerId);

        List<AssetDetailsDto> result = customerService.getCustomerAssets(customerId);

        assertEquals(2, result.size());
        assertEquals(asset2.getName(), result.get(0).name());
    }

    @Test
    void createCustomerShouldThrowCustomerAlreadyExistsException() {
        CreateCustomerDto createCustomerDto = new CreateCustomerDto(username, password, RoleEnum.ADMIN, "1000");
        Customer customer = createCustomer(customerId, username, password, adminRole);
        when(customerRepository.findCustomerByUsername(createCustomerDto.username())).thenReturn(Optional.of(customer));
        CustomerAlreadyExistException exception = assertThrows(
                CustomerAlreadyExistException.class,
                () -> customerService.createCustomer(createCustomerDto)
        );

        assertEquals("Customer with username: " + createCustomerDto.username() + " already exists", exception.getMessage());
    }

    @Test
    void createCustomerShouldReturnCustomerDetailsDto() {
        CreateCustomerDto createCustomerDto = new CreateCustomerDto(username, password, adminRole, "1000");
        when(customerRepository.findCustomerByUsername(createCustomerDto.username())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(createCustomerDto.password())).thenReturn("encodedPassword");

        CustomerDetailsDto result = customerService.createCustomer(createCustomerDto);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        verify(assetService).createAssetForCustomer(any(BigDecimal.class), any(Customer.class));

        Customer capturedCustomer = customerCaptor.getValue();
        assertEquals(createCustomerDto.username(), capturedCustomer.getUsername());
        assertEquals("encodedPassword", capturedCustomer.getPassword());
        assertEquals(createCustomerDto.role(), capturedCustomer.getRole());

        assertEquals(createCustomerDto.username(), result.username());
        assertEquals(createCustomerDto.role(), result.role());
    }
}
