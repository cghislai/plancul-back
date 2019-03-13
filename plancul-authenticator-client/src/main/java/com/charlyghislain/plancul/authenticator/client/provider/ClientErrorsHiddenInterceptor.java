package com.charlyghislain.plancul.authenticator.client.provider;

import com.charlyghislain.plancul.authenticator.client.exception.AuthenticatorClientError;
import com.charlyghislain.plancul.authenticator.client.exception.AuthenticatorClientValidationErrorException;
import com.charlyghislain.plancul.authenticator.client.exception.UserNotFoundException;
import com.charlyghislain.plancul.domain.exception.ValidationViolation;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.ws.rs.WebApplicationException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        JsonObject jsonObject;
        String errorCode;
        try {
            JsonReader jsonReader = Json.createReader((InputStream) responseStream);
            jsonObject = jsonReader.readObject();
            errorCode = jsonObject.getString("code");
        } catch (Throwable ignored) {
            return;
        }
        @Nullable JsonArray violations = jsonObject.getJsonArray("violations");
        if ("INVALID_PASSWORD".equals(errorCode)) {
            List<ValidationViolation> validationViolations = Optional.ofNullable(violations)
                    .map(this::listViolations)
                    .orElseGet(ArrayList::new);
            throw new AuthenticatorClientValidationErrorException(validationViolations);
        } else if ("USER_NOT_FOUND".equals(errorCode)) {
            throw new UserNotFoundException();
        } else if ("VALIDATION_ERROR".equals(errorCode)) {
            List<ValidationViolation> validationViolations = Optional.ofNullable(violations)
                    .map(this::listViolations)
                    .orElseGet(ArrayList::new);
            throw new AuthenticatorClientValidationErrorException(validationViolations);
        }
    }

    private List<ValidationViolation> listViolations(@NonNull JsonArray violations) {
        return violations.stream()
                .map(this::parseViolation)
                .collect(Collectors.toList());
    }

    private ValidationViolation parseViolation(JsonValue jsonValue) {
        JsonObject jsonObject = jsonValue.asJsonObject();
        String fieldName = jsonObject.getString("fieldName");
        String message = jsonObject.getString("message");

        return new ValidationViolation(fieldName, message);
    }
}
