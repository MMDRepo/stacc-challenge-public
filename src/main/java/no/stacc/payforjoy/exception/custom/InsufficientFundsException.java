package no.stacc.payforjoy.exception.custom;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String message) {
        super(message);
        log.warn("Insufficient funds: {}", message);
    }

    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
        log.warn("Insufficient funds: {} - Cause: {}", message, cause.getMessage());
    }
}

