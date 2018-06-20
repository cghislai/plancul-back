package com.charlyghislain.plancul.domain.util;

public enum PlanCulProperties {
    APP_NAME("com.charlyghislain.plancul.app.name"),
    APP_MAIL_FROM_NOREPLY("com.charlyghislain.plancul.app.mail.from.noreply"),
    APP_MAIL_SIGNATURE("com.charlyghislain.plancul.app.mail.signature"),
    APP_PROJECT_URL("com.charlyghislain.plancul.app.project.url"),
    APP_FRONT_URL("com.charlyghislain.plancul.app.front.url"),
    CONFIG_MAIL_ENABLED("com.charlyghislain.plancul.config.mail.enabled"),
    CONFIG_MAIL_RECIPIENT_WHITELIST_REGEX("com.charlyghislain.plancul.config.mail.recipient.whitelist.regex");

    private String key;

    PlanCulProperties(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
