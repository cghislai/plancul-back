package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.domain.util.WsError;

import javax.enterprise.context.ApplicationScoped;
import java.io.PrintWriter;
import java.io.StringWriter;

@ApplicationScoped
public class WsErrorConverter {

    public WsError toWsError(Throwable exception, boolean includeStackTrace) {
        String message = exception.getMessage();

        WsError wsError = new WsError();
        wsError.setMessage(message);

        if (includeStackTrace) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            exception.printStackTrace(printWriter);
            wsError.setStackTrace(stringWriter.toString());
        }

        return wsError;
    }
}
