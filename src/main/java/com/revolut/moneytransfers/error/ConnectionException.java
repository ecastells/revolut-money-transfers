package com.revolut.moneytransfers.error;

import com.revolut.moneytransfers.db.H2Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(H2Connection.class);

    public ConnectionException(String message) {
        super(message);
        log.error("Getting error on DB {}", message);
    }

    public ConnectionException(Throwable cause) {
        super(cause);
        log.error("Getting exception on DB {}", cause);
    }
}
