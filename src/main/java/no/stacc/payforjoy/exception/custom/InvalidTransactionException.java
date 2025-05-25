package no.stacc.payforjoy.exception.custom;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException(String message) {
        super(message);
        log.error("Invalid transaction: {}", message);
    }

    public InvalidTransactionException(String message, Throwable cause) {
        super(message, cause);
        log.error("Invalid transaction: {} - Cause: {}", message, cause.getMessage());
    }
}
