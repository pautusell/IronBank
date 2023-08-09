package com.ironhack.ironbank.exception;

public class UnregisteredTPException extends RuntimeException{
    public UnregisteredTPException(String hashedKey){
        super("Third Party provider not registered for Key: " + hashedKey);
    }
}
