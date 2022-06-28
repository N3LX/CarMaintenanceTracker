package com.n3lx.controller;

import com.n3lx.exception.ApiException;
import com.n3lx.exception.CustomSQLException;
import com.n3lx.exception.InputValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InputValidationException.class)
    protected ResponseEntity<Object> handleInputValidationException(InputValidationException exception) {
        ApiException apiException = new ApiException(exception.getMessage());
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomSQLException.class)
    protected ResponseEntity<Object> handleCustomSQLException(CustomSQLException exception) {
        ApiException apiException = new ApiException(exception.getMessage());
        return new ResponseEntity<>(apiException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
