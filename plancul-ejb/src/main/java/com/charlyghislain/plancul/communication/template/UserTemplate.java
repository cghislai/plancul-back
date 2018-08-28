package com.charlyghislain.plancul.communication.template;

import com.charlyghislain.dispatcher.api.context.TemplateContext;
import com.charlyghislain.dispatcher.api.context.TemplateField;

@TemplateContext(key = "user", produced = true)
public class UserTemplate {

    @TemplateField(description = "first name")
    private String firstName;
    @TemplateField(description = "last name")
    private String lastName;
    @TemplateField(description = "email")
    private String email;
    @TemplateField(description = "login")
    private String login;
    @TemplateField(description = "active")
    private Boolean active;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
