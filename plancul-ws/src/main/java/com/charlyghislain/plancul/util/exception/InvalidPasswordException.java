package com.charlyghislain.plancul.util.exception;

import javax.ws.rs.core.Response;

public class InvalidPasswordException extends WsException {

    public InvalidPasswordException() {
        super(Response.Status.BAD_REQUEST, "The provided password is invalid");
    }
}
