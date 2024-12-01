package com.smallstudy.error;

public class UnSupportedImageFileTypeException  extends RuntimeException {
    public UnSupportedImageFileTypeException(String message) {
        super(message);
    }

    public UnSupportedImageFileTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
