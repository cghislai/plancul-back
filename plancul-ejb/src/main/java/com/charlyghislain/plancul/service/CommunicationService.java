package com.charlyghislain.plancul.service;

import com.charlyghislain.dispatcher.api.context.TemplateContextObject;
import com.charlyghislain.dispatcher.api.dispatching.DispatchedMessage;
import com.charlyghislain.dispatcher.api.dispatching.DispatchingOption;
import com.charlyghislain.dispatcher.api.dispatching.DispatchingResult;
import com.charlyghislain.dispatcher.api.exception.MessageRenderingException;
import com.charlyghislain.dispatcher.api.message.DispatcherMessage;
import com.charlyghislain.dispatcher.api.message.Message;
import com.charlyghislain.dispatcher.api.rendering.ReadyToBeRenderedMessage;
import com.charlyghislain.dispatcher.api.rendering.ReadyToBeRenderedMessageBuilder;
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
import com.charlyghislain.plancul.domain.exception.PlanCulException;
import com.charlyghislain.plancul.domain.exception.PlanCulRuntimeException;
import com.charlyghislain.plancul.domain.security.AuthenticatorUser;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Stateless
public class CommunicationService {

    private final static Logger LOG = LoggerFactory.getLogger(CommunicationService.class);

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
        AuthenticatorUser authenticatorUser = userQueryService.findAuthenticatorUser(authenticatorUid)
                .orElseThrow(IllegalStateException::new);

        UserTemplate userTemplate = new UserTemplate();
        userTemplate.setActive(authenticatorUser.isActive());
        userTemplate.setEmail(authenticatorUser.getEmail());
        userTemplate.setFirstName(user.getFirstName());
        userTemplate.setLastName(user.getLastName());
        userTemplate.setLogin(authenticatorUser.getName());
        return userTemplate;
    }

    public DispatchedMessage sendAccountEmailVerification(User user, String verificationToken) throws PlanCulException {
        Locale userLocale = user.getLanguage().getLocale();
        UserTemplate userTemplate = this.createUserTemplate(user);
        EmailVerificationTemplate emailVerificationTemplate = this.createEmailVerificationTemplate(verificationToken, user);
        List<TemplateContextObject> templateContexts = templateContextsService.createTemplateContexts(
                accountEmailVerificationMessage, userTemplate, emailVerificationTemplate);

        ReadyToBeRenderedMessage readyToBeRenderedMessage = ReadyToBeRenderedMessageBuilder.newBuider(accountEmailVerificationMessage)
                .withContext(templateContexts)
                .acceptMailDispatching()
                .acceptLocale(userLocale)
                .build();

        DispatchedMessage dispatchedMessage = dispatchMessage(readyToBeRenderedMessage);
        String message = MessageFormat.format("email verification with token {0} for user {1}", verificationToken, user.getId());
        this.logDispatchingResult(message, dispatchedMessage);
        return dispatchedMessage;
    }


    public DispatchedMessage sendPasswordResetToken(User user, String resetToken) throws PlanCulException {
        Locale userLocale = user.getLanguage().getLocale();
        UserTemplate userTemplate = this.createUserTemplate(user);
        PasswordResetTemplate passwordResetTemplate = this.createPasswordResetTemplate(resetToken, user);
        List<TemplateContextObject> templateContexts = templateContextsService.createTemplateContexts(
                accountPasswordResetMessage, userTemplate, passwordResetTemplate);

        ReadyToBeRenderedMessage readyToBeRenderedMessage = ReadyToBeRenderedMessageBuilder.newBuider(accountPasswordResetMessage)
                .withContext(templateContexts)
                .acceptMailDispatching()
                .acceptLocale(userLocale)
                .build();

        DispatchedMessage dispatchedMessage = dispatchMessage(readyToBeRenderedMessage);
        String message = MessageFormat.format("password reset token for user {0}", user.getId());
        this.logDispatchingResult(message, dispatchedMessage);
        return dispatchedMessage;
    }

    @NotNull
    private DispatchedMessage dispatchMessage(ReadyToBeRenderedMessage readyToBeRenderedMessage) throws PlanCulException {
        RenderedMessage renderedMessage;
        try {
            renderedMessage = messageRenderer.renderMessage(readyToBeRenderedMessage);
        } catch (MessageRenderingException e) {
            throw new PlanCulException("Could not render message", e);
        }

        DispatchedMessage dispatchedMessage = messageDispatcher.dispatchMessage(renderedMessage);
        boolean anySucceeded = dispatchedMessage.isAnySucceeded();
        if (!anySucceeded) {
            throw new PlanCulException("Could not dispatch message", dispatchedMessage.getMultiErrorsException());
        }
        return dispatchedMessage;
    }

    private EmailVerificationTemplate createEmailVerificationTemplate(String token, User user) {
        AuthenticatorUser authenticatorUser = userQueryService.findAuthenticatorUser(user.getAuthenticatorUid())
                .orElseThrow(IllegalStateException::new);

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
        AuthenticatorUser authenticatorUser = userQueryService.findAuthenticatorUser(user.getAuthenticatorUid())
                .orElseThrow(IllegalStateException::new);

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


    private void logDispatchingResult(String messageName, DispatchedMessage dispatchedMessage) {
        boolean success = dispatchedMessage.isAnySucceeded();
        DispatcherMessage dispatcherMessage = dispatchedMessage.getMessage();
        String sucessDispatchedOptionsNames = dispatchedMessage.getDispatchingResultList().stream()
                .filter(DispatchingResult::isSuccess)
                .map(DispatchingResult::getDispatchingOption)
                .map(Enum::name)
                .collect(Collectors.joining(","));
        String failuresDispatchingOptionsNames = dispatchedMessage.getDispatchingResultList().stream()
                .filter(d -> !d.isSuccess())
                .map(DispatchingResult::getDispatchingOption)
                .map(Enum::name)
                .collect(Collectors.joining(","));

        if (success) {
            String infoMessage = MessageFormat.format("Sucessfully dispatched message {0} \"{1}\" by {2}",
                    dispatcherMessage.getName(), messageName, sucessDispatchedOptionsNames);
            LOG.info(infoMessage);
        } else {
            Exception error = dispatchedMessage.getMultiErrorsException();
            String warnMessage = MessageFormat.format("Failed to dispatch message {0} \"{1}\" by {2}",
                    dispatcherMessage.getName(), messageName, failuresDispatchingOptionsNames);
            LOG.warn(warnMessage, error);
        }
    }
}
