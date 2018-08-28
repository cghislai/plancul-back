package com.charlyghislain.plancul.communication.template;

import com.charlyghislain.dispatcher.api.context.TemplateContext;
import com.charlyghislain.dispatcher.api.context.TemplateField;

@TemplateContext(key = "emailVerification", produced = false)
public class EmailVerificationTemplate {

    @TemplateField(description = "Email verification token")
    private String token;
    @TemplateField(description = "Frontend url")
    private String frontendUrl;

    public EmailVerificationTemplate() {
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFrontendUrl() {
        return frontendUrl;
    }

    public void setFrontendUrl(String frontendUrl) {
        this.frontendUrl = frontendUrl;
    }
}
