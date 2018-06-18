package com.charlyghislain.plancul.util.exception;

import com.charlyghislain.plancul.converter.WsErrorConverter;
import com.charlyghislain.plancul.domain.util.WsError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ClientErrorExceptionMapper implements ExceptionMapper<ClientErrorException> {

    private final static Logger LOG = LoggerFactory.getLogger(ClientErrorExceptionMapper.class);
    @Inject
    private WsErrorConverter wsErrorConverter;

    @Override
    public Response toResponse(ClientErrorException exception) {
        WsError wsError = wsErrorConverter.toWsError(exception, false);

        Response response = Response.status(exception.getResponse().getStatus())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(wsError)
                .build();
        return response;
    }
}
