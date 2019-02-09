package com.revolut.moneytransfers.error;

public class ResponseError {

    private String errorMessage;

    public ResponseError(String errorMessage, String... args) {
        this.errorMessage = String.format(errorMessage, (Object) args);
    }

    public ResponseError(Exception ex) {
        this.errorMessage = ex.getMessage();
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
