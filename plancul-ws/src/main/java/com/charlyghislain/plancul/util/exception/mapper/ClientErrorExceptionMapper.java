package com.charlyghislain.plancul.util.exception.mapper;

import com.charlyghislain.plancul.converter.WsErrorConverter;
import com.charlyghislain.plancul.api.domain.util.WsError;
import com.charlyghislain.plancul.util.CrossOriginResourceSharingResponseFilter;

import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ClientErrorExceptionMapper implements ExceptionMapper<ClientErrorException> {

    @Inject
    private WsErrorConverter wsErrorConverter;
    @Inject
    private CrossOriginResourceSharingResponseFilter crossOriginResourceSharingResponseFilter;

    @Override
    public Response toResponse(ClientErrorException exception) {
        WsError wsError = wsErrorConverter.fromThrowable(exception);

        Response response = Response.status(exception.getResponse().getStatus())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(wsError)
                .build();
        crossOriginResourceSharingResponseFilter.filter(response);
        return response;
    }
    
}
