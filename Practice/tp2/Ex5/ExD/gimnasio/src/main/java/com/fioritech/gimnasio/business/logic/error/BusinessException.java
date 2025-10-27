package com.fioritech.gimnasio.business.logic.error;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
