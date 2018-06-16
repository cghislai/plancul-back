package com.charlyghislain.plancul.security.exception;

public class JwtValidationException extends Exception {
    public JwtValidationException() {
    }

    public JwtValidationException(String message) {
        super(message);
    }

    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtValidationException(Throwable cause) {
        super(cause);
    }

    public JwtValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
