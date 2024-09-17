package com.seckinyener.ing.broker.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerAlreadyExistException extends RuntimeException {

    public CustomerAlreadyExistException(String message) {
        super(message);
        log.error(message);
    }
}
