package com.charlyghislain.plancul.domain.api.request;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class WsPasswordResetRequest implements Serializable {

    @NotNull
    @Size(max = 400)
    private String password;
    @Nullable
    private String resetToken;
    @Nullable
    private String callerName;

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }
}
