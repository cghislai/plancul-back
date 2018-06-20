package com.charlyghislain.plancul.domain.util.exception;

public class MessageTemplateFileNotFoundException extends Exception {
    public MessageTemplateFileNotFoundException() {
    }

    public MessageTemplateFileNotFoundException(String message) {
        super(message);
    }

    public MessageTemplateFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageTemplateFileNotFoundException(Throwable cause) {
        super(cause);
    }

    public MessageTemplateFileNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
