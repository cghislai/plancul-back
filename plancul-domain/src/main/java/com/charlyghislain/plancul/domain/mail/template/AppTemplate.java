package com.charlyghislain.plancul.domain.mail.template;

public class AppTemplate {
    private String name;
    private String signature;
    private String projectUrl;
    private String noReplyEmail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public void setMailSignature(String signature) {
        this.signature = signature;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getNoReplyEmail() {
        return noReplyEmail;
    }

    public void setNoReplyEmail(String noReplyEmail) {
        this.noReplyEmail = noReplyEmail;
    }
}
