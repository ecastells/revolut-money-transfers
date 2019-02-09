package com.revolut.moneytransfers.error;

public class ConnectionException extends RuntimeException {

    public ConnectionException(Throwable cause) {
        super(cause);
    }
}
