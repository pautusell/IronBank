package com.ironhack.ironbank.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String username){
        super("The user: " + username + " was not found");
    }
}
