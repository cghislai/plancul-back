package com.charlyghislain.plancul.util;


import javax.ws.rs.core.Response;

public class UnsupportedLanguageWsException extends WsException {

    public UnsupportedLanguageWsException() {
        super(Response.Status.BAD_REQUEST, "Unsupported language");
    }
}
