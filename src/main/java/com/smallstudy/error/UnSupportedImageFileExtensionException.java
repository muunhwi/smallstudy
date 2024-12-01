package com.smallstudy.error;

public class UnSupportedImageFileExtensionException extends RuntimeException {
    public UnSupportedImageFileExtensionException(String message) {
        super(message);
    }
}
