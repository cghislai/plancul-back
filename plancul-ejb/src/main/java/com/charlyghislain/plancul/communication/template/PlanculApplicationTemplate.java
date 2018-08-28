package com.charlyghislain.plancul.communication.template;

import com.charlyghislain.dispatcher.api.context.TemplateContext;
import com.charlyghislain.dispatcher.api.context.TemplateField;

@TemplateContext(key = "app", produced = true)
public class PlanculApplicationTemplate {

    @TemplateField(description = "name")
    private String name;
    @TemplateField(description = "frontend url")
    private String frontendUrl;
    @TemplateField(description = "from email address")
    private String fromMail;
    @TemplateField(description = "projectUrl")
    private String projectUrl;
    @TemplateField(description = "contactMail")
    private String contactMail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrontendUrl() {
        return frontendUrl;
    }

    public void setFrontendUrl(String frontendUrl) {
        this.frontendUrl = frontendUrl;
    }

    public String getFromMail() {
        return fromMail;
    }

    public void setFromMail(String fromMail) {
        this.fromMail = fromMail;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getContactMail() {
        return contactMail;
    }

    public void setContactMail(String contactMail) {
        this.contactMail = contactMail;
    }
}
