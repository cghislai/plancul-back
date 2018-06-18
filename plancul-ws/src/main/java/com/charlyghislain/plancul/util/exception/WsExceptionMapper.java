package com.charlyghislain.plancul.util.exception;

import com.charlyghislain.plancul.converter.WsErrorConverter;
import com.charlyghislain.plancul.domain.security.ApplicationGroup;
import com.charlyghislain.plancul.domain.util.WsError;
import com.charlyghislain.plancul.util.WsException;

import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WsExceptionMapper implements ExceptionMapper<WsException> {

    @Inject
    private WsErrorConverter wsErrorConverter;
    @Inject
    private SecurityContext securityContext;

    @Override
    public Response toResponse(WsException exception) {
        Response.Status statusCode = exception.getStatusCode();

        boolean isAdmin = securityContext.isCallerInRole(ApplicationGroup.ADMIN.name());
        WsError wsError = wsErrorConverter.toWsError(exception, isAdmin);

        Response response = Response.status(statusCode)
                .entity(wsError)
                .build();
        return response;
    }
}
