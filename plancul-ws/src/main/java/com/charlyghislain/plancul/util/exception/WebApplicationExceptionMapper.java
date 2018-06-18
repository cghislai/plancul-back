package com.charlyghislain.plancul.util.exception;

import com.charlyghislain.plancul.converter.WsErrorConverter;
import com.charlyghislain.plancul.domain.util.WsError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<WebApplicationException> {

    private final static Logger LOG = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);
    @Inject
    private WsErrorConverter wsErrorConverter;

    @Override
    public Response toResponse(WebApplicationException exception) {
        WsError wsError = wsErrorConverter.toWsError(exception);

        LOG.error("Uncaught web application exception causing an error response to be sent to the client", exception);

        Response response = Response.status(exception.getResponse().getStatus())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(wsError)
                .build();
        return response;
    }
}
