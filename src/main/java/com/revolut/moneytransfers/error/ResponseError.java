package com.revolut.moneytransfers.error;

/**
 * Entity class that represent an error response.
 */
public class ResponseError {

    private String errorMessage;
    private ErrorCode errorCode;

    public ResponseError(String errorMessage, ErrorCode errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public ResponseError(Throwable ex, ErrorCode errorCode) {
        this.errorMessage = ex.getMessage();
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public enum ErrorCode {
        C001("Input Parameter Error"),
        C002("Validation Error"),
        C003("Database Error"),
        C500("General Error");

        String detail;

        ErrorCode(String detail) {
            this.detail = detail;
        }
    }
}
