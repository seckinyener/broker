package com.seckinyener.ing.broker.util;

import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.model.entity.Order;
import com.seckinyener.ing.broker.model.enumerated.RoleEnum;
import com.seckinyener.ing.broker.model.enumerated.SideEnum;
import com.seckinyener.ing.broker.model.enumerated.StatusEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public class TestConstants {

    public static Long customerId = 1L;

    public static String username = "Seckin Yener";

    public static String password = "test*0**";

    public static RoleEnum adminRole = RoleEnum.ADMIN;

    public static String assetName1 = "Asset1";

    public static String assetName2 = "Asset2";

    public static Long orderId = 1L;

    public static Customer createCustomer(Long customerId, String username, String password, RoleEnum role) {
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setUsername(username);
        customer.setPassword(password);
        customer.setRole(role);
        return customer;
    }

    public static Asset createAsset(String assetName, BigDecimal size, BigDecimal usableSize) {
        Asset asset = new Asset();
        asset.setName(assetName);
        asset.setSize(size);
        asset.setUsableSize(usableSize);
        return asset;
    }

    public static Order createOrder(SideEnum side, LocalDateTime createDate, String asset, StatusEnum status) {
        Order order = new Order();
        order.setOrderSide(side);
        order.setCreateDate(createDate);
        order.setStatus(status);
        order.setAsset(asset);
        return order;
    }

    public static List<Order> createOrdersForListAPI() {
        Order order1 = createOrder(SideEnum.BUY, LocalDate.of(2024, Month.SEPTEMBER, 15).atStartOfDay(), "THY", StatusEnum.PENDING);
        Order order2 = createOrder(SideEnum.SELL, LocalDate.of(2024, Month.SEPTEMBER, 17).atStartOfDay(), "AWS", StatusEnum.PENDING);
        Order order3 = createOrder(SideEnum.BUY, LocalDate.of(2024, Month.SEPTEMBER, 19).atStartOfDay(), "ISB", StatusEnum.PENDING);
        return List.of(order1, order2, order3);
    }
}
