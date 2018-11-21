package com.charlyghislain.plancul.astronomy.client.almanac.exception;

public class AlmanacClientError extends Exception {
    public AlmanacClientError() {
    }

    public AlmanacClientError(String message) {
        super(message);
    }

    public AlmanacClientError(String message, Throwable cause) {
        super(message, cause);
    }

    public AlmanacClientError(Throwable cause) {
        super(cause);
    }
}
