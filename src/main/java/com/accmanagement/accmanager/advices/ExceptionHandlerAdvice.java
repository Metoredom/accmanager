package com.accmanagement.accmanager.advices;

import com.accmanagement.accmanager.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> insufficientBalanceHandler(InsufficientBalanceException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> accountNotFoundHandler(AccountNotFoundException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(ClientNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> clientNotFoundHandler(ClientNotFoundException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(CurrencyMismatchingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> currencyMismatchingHandler(CurrencyMismatchingException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(CurrencyConversionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> currencyConversionFailedException(CurrencyConversionException e){
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<?> serviceNotAvailableHandler(ServiceUnavailableException e){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException e){
        String errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(errors);
    }
}
