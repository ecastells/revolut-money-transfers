package com.revolut.moneytransfers.error;

import com.revolut.moneytransfers.db.H2Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception generated when an error occurs during validation of input parameter.
 */
public class ValidationException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(H2Connection.class);

    public ValidationException(String message) {
        super(message);
        log.warn("Validation error {}", message);
    }
}
