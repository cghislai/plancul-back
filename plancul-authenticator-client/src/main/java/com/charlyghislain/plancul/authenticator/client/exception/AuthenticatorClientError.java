package com.charlyghislain.plancul.authenticator.client.exception;

public class AuthenticatorClientError extends Exception {
    public AuthenticatorClientError() {
    }

    public AuthenticatorClientError(String message) {
        super(message);
    }

    public AuthenticatorClientError(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticatorClientError(Throwable cause) {
        super(cause);
    }
}
