package com.lithan.abcjobs.exception;

public class CredentialAlreadyTakenException extends RuntimeException{
    public CredentialAlreadyTakenException(String message) {
        super(message);
    }
}
