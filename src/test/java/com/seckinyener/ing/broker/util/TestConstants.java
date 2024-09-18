package com.seckinyener.ing.broker.util;

import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.model.enumerated.RoleEnum;

import java.math.BigDecimal;

public class TestConstants {

    public static Long customerId = 1L;

    public static String username = "Seckin Yener";

    public static String password = "test*0**";

    public static RoleEnum adminRole = RoleEnum.ADMIN;

    public static String assetName1 = "Asset1";

    public static String assetName2 = "Asset2";

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
}
