package com.ironhack.ironbank.advice;

import com.ironhack.ironbank.exception.OperationalException;
import com.ironhack.ironbank.exception.UnauthorizedException;
import com.ironhack.ironbank.exception.UnregisteredTPException;
import com.ironhack.ironbank.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String elementNotFoundHandler(NoSuchElementException ex){
        return ex.getMessage();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String userNotFoundHandler(UserNotFoundException ex){
        return ex.getMessage();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String unauthorizedAccountHandler(UnauthorizedException ex){
        return ex.getMessage();
    }

    @ExceptionHandler(OperationalException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String operationalExceptionHandler(OperationalException ex){
        return ex.getMessage();
    }

    @ExceptionHandler(UnregisteredTPException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String unregisteredTP(UnregisteredTPException ex){
        return ex.getMessage();
    }

    @ExceptionHandler(WebExchangeBindException.class) // MethodArgumentNotValidException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(WebExchangeBindException ex) {
        var errors = new HashMap<String, String>();
        ex.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
