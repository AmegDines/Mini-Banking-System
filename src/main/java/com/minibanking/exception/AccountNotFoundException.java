package com.minibanking.exception;
public class AccountNotFoundException extends IllegalArgumentException {
    public AccountNotFoundException(String message) { super(message); }
}
