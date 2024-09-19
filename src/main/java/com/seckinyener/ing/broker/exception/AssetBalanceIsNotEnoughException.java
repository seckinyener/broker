package com.seckinyener.ing.broker.exception;

public class AssetBalanceIsNotEnoughException extends RuntimeException {
    public AssetBalanceIsNotEnoughException(String message) {
        super(message);
    }
}
