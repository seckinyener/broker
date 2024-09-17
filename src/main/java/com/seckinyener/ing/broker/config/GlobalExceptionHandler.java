package com.seckinyener.ing.broker.config;

import com.seckinyener.ing.broker.exception.CustomerAlreadyExistException;
import com.seckinyener.ing.broker.model.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponseDto handleUserAlreadyExistException(CustomerAlreadyExistException e) {
        return new ErrorResponseDto(e.getMessage(), HttpStatus.CONFLICT.value(), LocalDateTime.now());
    }
}
