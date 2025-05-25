package no.stacc.payforjoy.exception.custom;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String message) {
        super(message);
        log.error("Account not found: {}", message);
    }

    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
        log.error("Account not found: {} - Cause: {}", message, cause.getMessage());
    }
}

