package com.smallstudy.error;

public class RuntimeAccessDeniedException extends RuntimeException {

    public RuntimeAccessDeniedException() {
    }

    public RuntimeAccessDeniedException(String message) {
        super(message);
    }
}
