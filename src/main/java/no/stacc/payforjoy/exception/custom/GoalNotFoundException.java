package no.stacc.payforjoy.exception.custom;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoalNotFoundException extends RuntimeException {

    public GoalNotFoundException(String message) {
        super(message);
        log.error("Savings goal not found: {}", message);
    }

    public GoalNotFoundException(String message, Throwable cause) {
        super(message, cause);
        log.error("Savings goal not found: {} - Cause: {}", message, cause.getMessage());
    }
}
