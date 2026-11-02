package com.minibanking.exception;
public class InsufficientBalanceException extends IllegalArgumentException {
    public InsufficientBalanceException(String message) { super(message); }
}
