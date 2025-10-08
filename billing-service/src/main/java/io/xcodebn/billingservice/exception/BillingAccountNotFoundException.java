package io.xcodebn.billingservice.exception;

public class BillingAccountNotFoundException extends RuntimeException {
    public BillingAccountNotFoundException(String message) {
        super(message);
    }
}