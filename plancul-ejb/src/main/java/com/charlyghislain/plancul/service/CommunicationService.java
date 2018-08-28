package com.charlyghislain.plancul.service;

import com.charlyghislain.dispatcher.api.context.TemplateContextObject;
import com.charlyghislain.dispatcher.api.dispatching.DispatchedMessage;
import com.charlyghislain.dispatcher.api.dispatching.DispatchingOption;
import com.charlyghislain.dispatcher.api.dispatching.DispatchingResult;
import com.charlyghislain.dispatcher.api.exception.DispatcherException;
import com.charlyghislain.dispatcher.api.header.MailHeadersTemplate;
import com.charlyghislain.dispatcher.api.message.DispatcherMessage;
import com.charlyghislain.dispatcher.api.message.Message;
import com.charlyghislain.dispatcher.api.rendering.ReadyToBeRenderedMessage;
import com.charlyghislain.dispatcher.api.rendering.RenderedMessage;
import com.charlyghislain.dispatcher.api.service.MessageDispatcher;
import com.charlyghislain.dispatcher.api.service.MessageRenderer;
import com.charlyghislain.dispatcher.api.service.MessageResourcesService;
import com.charlyghislain.dispatcher.api.service.TemplateContextsService;
import com.charlyghislain.plancul.communication.message.AccountEmailVerificationMessage;
import com.charlyghislain.plancul.communication.message.AccountPasswordResetMessage;
import com.charlyghislain.plancul.communication.template.EmailVerificationTemplate;
import com.charlyghislain.plancul.communication.template.PasswordResetTemplate;
import com.charlyghislain.plancul.communication.template.UserTemplate;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.config.ConfigConstants;
import com.charlyghislain.plancul.domain.exception.PlanCulRuntimeException;
import com.charlyghislain.plancul.domain.security.AuthenticatorUser;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Stateless
public class CommunicationService {

    @Inject
    private TemplateContextsService templateContextsService;
    @Inject
    private MessageResourcesService messageResourcesService;
    @Inject
    private MessageRenderer messageRenderer;
    @Inject
    private MessageDispatcher messageDispatcher;

    @Inject
    private UserQueryService userQueryService;

    @Inject
    @Message(AccountEmailVerificationMessage.class)
    private DispatcherMessage accountEmailVerificationMessage;
    @Inject
    @Message(AccountPasswordResetMessage.class)
    private DispatcherMessage accountPasswordResetMessage;


    @Inject
    @ConfigProperty(name = ConfigConstants.APPLICATION_FRONTEND_URL)
    private String applicationFrontendUrl;

    @Inject
    @ConfigProperty(name = ConfigConstants.APPLICATION_FRONTEND_ACCOUNT_ACTIVATION_PATH)
    private String frontendAccountActivationPath;
    @Inject
    @ConfigProperty(name = ConfigConstants.APPLICATION_FRONTEND_ACCOUNT_ACTIVATION_EMAIL_TOKEN_PARAM)
    private String frontendAccountActivationTokenParam;
    @Inject
    @ConfigProperty(name = ConfigConstants.APPLICATION_FRONTEND_ACCOUNT_ACTIVATION_EMAIL_EMAIL_PARAM)
    private String frontendAccountActivationEmailParam;

    @Inject
    @ConfigProperty(name = ConfigConstants.APPLICATION_FRONTEND_PASSWORD_RESET_PATH)
    private String frontendPasswordResetPath;
    @Inject
    @ConfigProperty(name = ConfigConstants.APPLICATION_FRONTEND_PASSWORD_RESET_TOKEN_PARAM)
    private String frontendPasswordResetTokenParam;
    @Inject
    @ConfigProperty(name = ConfigConstants.APPLICATION_FRONTEND_PASSWORD_RESET_EMAIL_PARAM)
    private String frontendPasswordResetEmailParam;


    public UserTemplate createUserTemplate(User user) {
        Long authenticatorUid = user.getAuthenticatorUid();
        AuthenticatorUser authenticatorUser = userQueryService.findAuthenticatorUser(authenticatorUid);

        UserTemplate userTemplate = new UserTemplate();
        userTemplate.setActive(authenticatorUser.isActive());
        userTemplate.setEmail(authenticatorUser.getEmail());
        userTemplate.setFirstName(user.getFirstName());
        userTemplate.setLastName(user.getLastName());
        userTemplate.setLogin(authenticatorUser.getName());
        return userTemplate;
    }

    public DispatchingResult sendAccountEmailVerification(User user, String verificationToken) {
        try {
            Locale locale = user.getLanguage().getLocale();
            List<Locale> acceptedLocales = Collections.singletonList(locale);
            Set<DispatchingOption> dispatchingOptions = Collections.singleton(DispatchingOption.MAIL_HTML);

            UserTemplate userTemplate = this.createUserTemplate(user);
            EmailVerificationTemplate emailVerificationTemplate = this.createEmailVerificationTemplate(verificationToken, user);
            List<TemplateContextObject> templateContexts = templateContextsService.createTemplateContexts(accountEmailVerificationMessage, userTemplate, emailVerificationTemplate);
            MailHeadersTemplate mailMessageHeaders = messageResourcesService.findMailMessageHeaders(accountEmailVerificationMessage, locale);

            ReadyToBeRenderedMessage readyToBeRenderedMessage = new ReadyToBeRenderedMessage();
            readyToBeRenderedMessage.setAcceptedLocales(acceptedLocales);
            readyToBeRenderedMessage.setContextObjects(templateContexts);
            readyToBeRenderedMessage.setDispatchingOptions(dispatchingOptions);
            readyToBeRenderedMessage.setMailHeadersTemplate(mailMessageHeaders);
            readyToBeRenderedMessage.setMessage(accountEmailVerificationMessage);


            return dispatchHtmlMessage(readyToBeRenderedMessage);
        } catch (DispatcherException e) {
            throw new PlanCulRuntimeException(e);
        }
    }

    public DispatchingResult sendPasswordResetToken(User user, String resetToken) {
        try {
            Locale locale = user.getLanguage().getLocale();
            List<Locale> acceptedLocales = Collections.singletonList(locale);
            Set<DispatchingOption> dispatchingOptions = Collections.singleton(DispatchingOption.MAIL_HTML);

            UserTemplate userTemplate = this.createUserTemplate(user);
            PasswordResetTemplate passwordResetTemplate = this.createPasswordResetTemplate(resetToken, user);
            List<TemplateContextObject> templateContexts = templateContextsService.createTemplateContexts(accountPasswordResetMessage, userTemplate, passwordResetTemplate);
            MailHeadersTemplate mailMessageHeaders = messageResourcesService.findMailMessageHeaders(accountPasswordResetMessage, locale);

            ReadyToBeRenderedMessage readyToBeRenderedMessage = new ReadyToBeRenderedMessage();
            readyToBeRenderedMessage.setAcceptedLocales(acceptedLocales);
            readyToBeRenderedMessage.setContextObjects(templateContexts);
            readyToBeRenderedMessage.setDispatchingOptions(dispatchingOptions);
            readyToBeRenderedMessage.setMailHeadersTemplate(mailMessageHeaders);
            readyToBeRenderedMessage.setMessage(accountPasswordResetMessage);


            return dispatchHtmlMessage(readyToBeRenderedMessage);
        } catch (DispatcherException e) {
            throw new PlanCulRuntimeException(e);
        }
    }

    @NotNull
    private DispatchingResult dispatchHtmlMessage(ReadyToBeRenderedMessage readyToBeRenderedMessage) {
        try {
            RenderedMessage renderedMessage = messageRenderer.renderMessage(readyToBeRenderedMessage);

            DispatchedMessage dispatchedMessage = messageDispatcher.dispatchMessage(renderedMessage);
            DispatchingResult dispatchingResult = dispatchedMessage.getDispatchingResults().get(DispatchingOption.MAIL_HTML);
            if (!dispatchingResult.isSuccess()) {
                throw new PlanCulRuntimeException(dispatchingResult.getError());
            }
            return dispatchingResult;
        } catch (DispatcherException e) {
            throw new PlanCulRuntimeException(e);
        }
    }

    private EmailVerificationTemplate createEmailVerificationTemplate(String token, User user) {
        AuthenticatorUser authenticatorUser = userQueryService.findAuthenticatorUser(user.getAuthenticatorUid());
        String email = authenticatorUser.getEmail();
        try {
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.name());
            String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8.name());

            String activationUrl = MessageFormat.format("{0}/{1}?{2}={3}&{4}={5}",
                    this.applicationFrontendUrl, this.frontendAccountActivationPath,
                    this.frontendAccountActivationTokenParam, encodedToken,
                    this.frontendAccountActivationEmailParam, encodedEmail);

            EmailVerificationTemplate emailVerificationTemplate = new EmailVerificationTemplate();
            emailVerificationTemplate.setFrontendUrl(activationUrl);
            emailVerificationTemplate.setToken(token);
            return emailVerificationTemplate;
        } catch (UnsupportedEncodingException e) {
            throw new PlanCulRuntimeException(e);
        }
    }

    private PasswordResetTemplate createPasswordResetTemplate(String token, User user) {
        AuthenticatorUser authenticatorUser = userQueryService.findAuthenticatorUser(user.getAuthenticatorUid());
        String email = authenticatorUser.getEmail();
        try {
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.name());
            String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8.name());

            String activationUrl = MessageFormat.format("{0}/{1}?{2}={3}&{4}={5}",
                    this.applicationFrontendUrl, this.frontendPasswordResetPath,
                    this.frontendPasswordResetTokenParam, encodedToken,
                    this.frontendPasswordResetEmailParam, encodedEmail);

            PasswordResetTemplate passwordResetTemplate = new PasswordResetTemplate();
            passwordResetTemplate.setFrontendUrl(activationUrl);
            passwordResetTemplate.setToken(token);
            return passwordResetTemplate;
        } catch (UnsupportedEncodingException e) {
            throw new PlanCulRuntimeException(e);
        }
    }
}
