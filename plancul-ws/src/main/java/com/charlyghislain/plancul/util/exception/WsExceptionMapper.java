package com.charlyghislain.plancul.util.exception;

import com.charlyghislain.plancul.converter.WsErrorConverter;
import com.charlyghislain.plancul.domain.util.WsError;
import com.charlyghislain.plancul.util.CrossOriginResourceSharingResponseFilter;
import com.charlyghislain.plancul.util.WsException;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WsExceptionMapper implements ExceptionMapper<WsException> {

    @Inject
    private WsErrorConverter wsErrorConverter;
    @Inject
    private CrossOriginResourceSharingResponseFilter crossOriginResourceSharingResponseFilter;

    @Override
    public Response toResponse(WsException exception) {
        Response.Status statusCode = exception.getStatusCode();

        WsError wsError = wsErrorConverter.toWsError(exception);

        Response response = Response.status(statusCode)
                .entity(wsError)
                .build();
        crossOriginResourceSharingResponseFilter.filter(response);
        return response;
    }
}
