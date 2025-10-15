package com.tienda.app.business.logic.error;

public class ErrorServiceException extends Exception {

    public ErrorServiceException(String message) {
        super(message);
    }

    public ErrorServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
