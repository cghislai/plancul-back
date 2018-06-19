package com.charlyghislain.plancul.util.exception;

import com.charlyghislain.plancul.converter.WsErrorConverter;
import com.charlyghislain.plancul.domain.util.WsError;
import com.charlyghislain.plancul.domain.util.exception.PlanCulRuntimeException;
import com.charlyghislain.plancul.util.CrossOriginResourceSharingResponseFilter;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class PlanCulRuntimeExceptionMapper implements ExceptionMapper<PlanCulRuntimeException> {

    @Inject
    private WsErrorConverter wsErrorConverter;
    @Inject
    private CrossOriginResourceSharingResponseFilter crossOriginResourceSharingResponseFilter;

    @Override
    public Response toResponse(PlanCulRuntimeException exception) {
        int httpStatusCode = exception.getHttpStatusCode()
                .orElse(500);

        WsError wsError = wsErrorConverter.toWsError(exception);

        Response response = Response.status(httpStatusCode)
                .entity(wsError)
                .build();
        crossOriginResourceSharingResponseFilter.filter(response);
        return response;
    }
}
