package com.charlyghislain.plancul.util.exception.mapper;

import com.charlyghislain.plancul.converter.WsErrorConverter;
import com.charlyghislain.plancul.domain.api.response.WsValidationError;
import com.charlyghislain.plancul.util.CrossOriginResourceSharingResponseFilter;
import com.charlyghislain.plancul.util.exception.WsValidationException;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WsValidationExceptionMapper implements ExceptionMapper<WsValidationException> {

    @Inject
    private WsErrorConverter wsErrorConverter;
    @Inject
    private CrossOriginResourceSharingResponseFilter crossOriginResourceSharingResponseFilter;

    @Override
    public Response toResponse(WsValidationException exception) {
        Response.Status statusCode = exception.getStatusCode();

        WsValidationError validationError = wsErrorConverter.fromValidationException(exception);

        Response response = Response.status(statusCode)
                .entity(validationError)
                .build();
        crossOriginResourceSharingResponseFilter.filter(response);
        return response;
    }
}
