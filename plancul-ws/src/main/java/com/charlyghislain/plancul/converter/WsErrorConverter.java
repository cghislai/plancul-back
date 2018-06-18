package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.domain.util.WsError;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WsErrorConverter {

    public WsError toWsError(Throwable exception) {
        String message = exception.getMessage();

        WsError wsError = new WsError();
        wsError.setMessage(message);

        return wsError;
    }
}
