package com.charlyghislain.plancul.util.exception;

import javax.ws.rs.core.Response;

public class ReferenceNotFoundException extends WsException {

    public ReferenceNotFoundException() {
        super(Response.Status.NOT_FOUND, "A reference could not be found");
    }
}
