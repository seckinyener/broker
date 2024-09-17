package com.seckinyener.ing.broker.exception;

public class OrderStatusNotValidForDeletingException extends RuntimeException {
    public OrderStatusNotValidForDeletingException(String message) {
        super(message);
    }
}
