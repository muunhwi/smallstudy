package com.smallstudy.error;

public class IORuntimeException extends RuntimeException {


    public IORuntimeException(Throwable cause) {
        super(cause);
    }

    public IORuntimeException() {
    }

    public IORuntimeException(String message) {
        super(message);
    }
}
