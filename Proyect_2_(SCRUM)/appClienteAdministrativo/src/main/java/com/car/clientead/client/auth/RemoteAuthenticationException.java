package com.car.clientead.client.auth;

public class RemoteAuthenticationException extends RuntimeException {

    public RemoteAuthenticationException(String message) {
        super(message);
    }

    public RemoteAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
