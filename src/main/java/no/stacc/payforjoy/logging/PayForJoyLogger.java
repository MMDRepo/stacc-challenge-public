package no.stacc.payforjoy.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PayForJoyLogger {

    private final Logger logger = LoggerFactory.getLogger(PayForJoyLogger.class);

    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    public void error(String message, Object... args) {
        logger.error(message, args);
    }

    public void error(String message, Throwable throwable, Object... args) {
        logger.error(String.format(message, args), throwable);
    }

    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }
}

