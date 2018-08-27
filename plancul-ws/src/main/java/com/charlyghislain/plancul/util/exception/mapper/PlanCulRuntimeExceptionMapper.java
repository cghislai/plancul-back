package com.charlyghislain.plancul.util.exception.mapper;

import com.charlyghislain.plancul.api.domain.util.WsError;
import com.charlyghislain.plancul.converter.WsErrorConverter;
import com.charlyghislain.plancul.domain.exception.PlanCulRuntimeException;
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

        WsError wsError = wsErrorConverter.fromThrowable(exception);

        Response response = Response.status(500)
                .entity(wsError)
                .build();
        crossOriginResourceSharingResponseFilter.filter(response);
        return response;
    }
}
