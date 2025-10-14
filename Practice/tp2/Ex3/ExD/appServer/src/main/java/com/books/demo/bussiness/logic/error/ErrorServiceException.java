package com.books.demo.bussiness.logic.error;

/**
 * Excepción de capa de servicio que permite distinguir errores funcionales de errores de sistema.
 */
public class ErrorServiceException extends Exception {

    public ErrorServiceException(String message) {
        super(message);
    }

    public ErrorServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
