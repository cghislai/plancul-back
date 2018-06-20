package com.charlyghislain.plancul.domain.request;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class WsUserAccountInitRequest implements Serializable {

    @NotNull
    private String email;
    @NotNull
    private String passwordToken;
    @NotNull
    private String password;

    @NotNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    @NotNull
    public String getPasswordToken() {
        return passwordToken;
    }

    public void setPasswordToken(@NotNull String passwordToken) {
        this.passwordToken = passwordToken;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }
}
