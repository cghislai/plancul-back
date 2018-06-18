package com.charlyghislain.plancul.util;


import javax.ws.rs.core.Response;

public class WsException extends RuntimeException {

    private Response.Status statusCode;

    public WsException(Response.Status statusCode) {
        this.statusCode = statusCode;
    }

    public WsException(Response.Status statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public Response.Status getStatusCode() {
        return statusCode;
    }
}
