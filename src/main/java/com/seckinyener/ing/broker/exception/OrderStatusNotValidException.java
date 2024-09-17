package com.seckinyener.ing.broker.exception;

public class OrderStatusNotValidException extends RuntimeException {
    public OrderStatusNotValidException(String message) {
        super(message);
    }
}
