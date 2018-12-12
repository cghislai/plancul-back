package com.charlyghislain.plancul.authenticator.client.provider;

import com.charlyghislain.authenticator.management.api.domain.WsError;
import com.charlyghislain.authenticator.management.api.domain.WsValidationError;
import com.charlyghislain.authenticator.management.api.domain.WsViolationError;
import com.charlyghislain.plancul.authenticator.client.converter.ValidationViolationConverter;
import com.charlyghislain.plancul.authenticator.client.exception.AuthenticatorClientError;
import com.charlyghislain.plancul.authenticator.client.exception.InvalidPasswordException;
import com.charlyghislain.plancul.authenticator.client.exception.UserNotFoundException;
import com.charlyghislain.plancul.authenticator.client.exception.AuthenticatorClientValidationErrorException;
import com.charlyghislain.plancul.domain.exception.ValidationViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.json.*;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.io.InputStream;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@ClientErrorsHidden
@Interceptor
public class ClientErrorsHiddenInterceptor {

    private final static Logger LOG = LoggerFactory.getLogger(ClientErrorsHiddenInterceptor.class);

    @Inject
    private ValidationViolationConverter validationViolationConverter;

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
        JsonObject jsonObject;
        String errorCode;
        try {
            JsonReader jsonReader = Json.createReader((InputStream) responseStream);
            jsonObject = jsonReader.readObject();
            errorCode = jsonObject.getString("code");
        } catch (Throwable ignored) {
            return;
        }
        if ("INVALID_PASSWORD".equals(errorCode)) {
            List<ValidationViolation> validationViolations = jsonObject.getJsonArray("violations").stream()
                    .map(this::parseViolation)
                    .collect(Collectors.toList());
            throw new AuthenticatorClientValidationErrorException(validationViolations);
        } else if ("USER_NOT_FOUND".equals(errorCode)) {
            throw new UserNotFoundException();
        } else if ("VALIDATION_ERROR".equals(errorCode)) {
            List<ValidationViolation> validationViolations = jsonObject.getJsonArray("violations").stream()
                    .map(this::parseViolation)
                    .collect(Collectors.toList());
            throw new AuthenticatorClientValidationErrorException(validationViolations);
        }
    }

    private ValidationViolation parseViolation(JsonValue jsonValue) {
        JsonObject jsonObject = jsonValue.asJsonObject();
        String fieldName = jsonObject.getString("fieldName");
        String message = jsonObject.getString("message");

        return new ValidationViolation(fieldName, message);
    }
}
