package com.charlyghislain.plancul.api.domain.request;

import javax.validation.constraints.NotNull;

public class WsPasswordReset {

    @NotNull
    private String resetToken;
    @NotNull
    private String email;
    @NotNull
    private String password;

    @NotNull
    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(@NotNull String resetToken) {
        this.resetToken = resetToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }
}
