package com.charlyghislain.plancul.util.exception;

import com.charlyghislain.plancul.converter.WsErrorConverter;
import com.charlyghislain.plancul.domain.api.util.WsError;
import com.charlyghislain.plancul.util.CrossOriginResourceSharingResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UncaughtExceptionMapper implements ExceptionMapper<Throwable> {

    private final static Logger LOG = LoggerFactory.getLogger(UncaughtExceptionMapper.class);
    @Inject
    private WsErrorConverter wsErrorConverter;
    @Inject
    private CrossOriginResourceSharingResponseFilter crossOriginResourceSharingResponseFilter;

    @Override
    public Response toResponse(Throwable exception) {
        WsError wsError = wsErrorConverter.toWsError(exception);

        LOG.error("Uncaught exception causing an error 500 response to be sent to the client", exception);

        Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(wsError)
                .build();
        crossOriginResourceSharingResponseFilter.filter(response);
        return response;
    }
}
