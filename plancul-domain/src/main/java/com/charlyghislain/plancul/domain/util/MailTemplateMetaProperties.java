package com.charlyghislain.plancul.domain.util;

public enum MailTemplateMetaProperties {
    FROM("mail.from", true),
    TO("mail.to", true),
    CC("mail.cc", false),
    BCC("mail.bcc", false),
    SUBJECT("mail.subject", true);

    private String key;
    private boolean required;

    MailTemplateMetaProperties(String key, boolean required) {
        this.key = key;
        this.required = required;
    }

    public String getKey() {
        return key;
    }

    public boolean isRequired() {
        return required;
    }
}
