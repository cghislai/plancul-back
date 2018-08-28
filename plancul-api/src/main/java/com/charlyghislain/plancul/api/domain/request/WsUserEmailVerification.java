package com.charlyghislain.plancul.api.domain.request;

import com.charlyghislain.plancul.api.domain.util.NullableField;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;

public class WsUserEmailVerification {

    @NotNull
    private String verificationToken;
    @NotNull
    private String email;

    @NotNull
    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(@NotNull String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
