package com.ironhack.ironbank.exception;

public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException (Long accountId){
        super("Operation not allowed: You are not authorized for account: " + accountId);
    }
}
