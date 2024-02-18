package com.accmanagement.accmanager.advices;

import com.accmanagement.accmanager.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<?> insufficientBalanceHandler(InsufficientBalanceException e){
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<?> accountNotFoundHandler(AccountNotFoundException e){
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<?> clientNotFoundHandler(ClientNotFoundException e){
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ExceptionHandler(CurrencyMismatchingException.class)
    public ResponseEntity<?> currencyMismatchingHandler(CurrencyMismatchingException e){
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ExceptionHandler(CurrencyConversionFailedException.class)
    public ResponseEntity<?> currencyConversionFailedException(CurrencyConversionFailedException e){
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<?> serviceNotAvailableHandler(ServiceUnavailableException e){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }

}
