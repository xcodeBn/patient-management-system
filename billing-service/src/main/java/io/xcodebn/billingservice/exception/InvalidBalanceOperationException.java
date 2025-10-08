package io.xcodebn.billingservice.exception;

public class InvalidBalanceOperationException extends RuntimeException {
    public InvalidBalanceOperationException(String message) {
        super(message);
    }
}