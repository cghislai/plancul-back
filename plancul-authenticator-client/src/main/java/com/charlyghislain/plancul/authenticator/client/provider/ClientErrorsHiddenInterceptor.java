package com.charlyghislain.plancul.authenticator.client.provider;

import com.charlyghislain.authenticator.management.api.domain.WsError;
import com.charlyghislain.plancul.authenticator.client.exception.AuthenticatorClientError;
import com.charlyghislain.plancul.authenticator.client.exception.InvalidPasswordException;
import com.charlyghislain.plancul.authenticator.client.exception.UserNotFoundException;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.MessageFormat;

@ClientErrorsHidden
@Interceptor
public class ClientErrorsHiddenInterceptor {

    private final static Logger LOG = LoggerFactory.getLogger(ClientErrorsHiddenInterceptor.class);

    @AroundInvoke
    public Object hideClientErrors(InvocationContext invocationContext) throws Exception {
        try {
            return invocationContext.proceed();
        } catch (WebApplicationException e) {
            tryReadExceptionBody(e);

            Method method = invocationContext.getMethod();
            String methodName = method.getName();
            Object target = invocationContext.getTarget();
            String targetSimpleName = target.getClass().getSimpleName();
            String message = MessageFormat.format("Client error while invoking {0} on {1}", methodName, targetSimpleName);
            LOG.warn(message, e);

            throw new AuthenticatorClientError();
        }
    }

    private void tryReadExceptionBody(WebApplicationException e) throws Exception {
        Object responseStream = e.getResponse().getEntity();
        WsError wsError;
        try {
            Jsonb jsonb = JsonbBuilder.create();
            wsError = jsonb.fromJson((InputStream) responseStream, WsError.class);
        } catch (Throwable ignored) {
            return;
        }
        if ("INVALID_PASSWORD".equals(wsError.getCode())) {
            throw new InvalidPasswordException();
        } else if ("USER_NOT_FOUND".equals(wsError.getCode())) {
            throw new UserNotFoundException();
        }
    }
}
