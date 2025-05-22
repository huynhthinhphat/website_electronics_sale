package com.tip.b18.electronicsales.exceptions;

public class IntegrityConstraintViolationException extends RuntimeException {
    public IntegrityConstraintViolationException(String message) {
        super(message);
    }
}
