package com.lithan.abcjobs.exception;

public class UserNotActivatedException extends RuntimeException{
    public UserNotActivatedException(String message) {
        super(message);
    }
}
