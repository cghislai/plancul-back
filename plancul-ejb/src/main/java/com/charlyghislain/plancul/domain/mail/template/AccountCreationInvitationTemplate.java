package com.charlyghislain.plancul.domain.mail.template;

@VelocityTemplate("mail/account-creation-invitation")
public class AccountCreationInvitationTemplate implements MailTemplate {

    public AppTemplate app;
    public UserTemplate user;
    public AccountTemplate account;


    public AppTemplate getApp() {
        return app;
    }

    public void setApp(AppTemplate app) {
        this.app = app;
    }

    public UserTemplate getUser() {
        return user;
    }

    public void setUser(UserTemplate user) {
        this.user = user;
    }

    public AccountTemplate getAccount() {
        return account;
    }

    public void setAccount(AccountTemplate account) {
        this.account = account;
    }
}
