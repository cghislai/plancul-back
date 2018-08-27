package com.charlyghislain.plancul.api.domain.response;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class WsContraintViolation implements Serializable {

    @Nullable
    private String propertyName;
    @NotNull
    private String message;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyPath(String propertyName) {
        this.propertyName = propertyName;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    public void setMessage(@NotNull String message) {
        this.message = message;
    }
}
