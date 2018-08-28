package com.charlyghislain.plancul.communication;

import com.charlyghislain.dispatcher.api.context.ProducedTemplateContext;
import com.charlyghislain.plancul.communication.template.PlanculApplicationTemplate;
import com.charlyghislain.plancul.communication.template.UserTemplate;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.config.ConfigConstants;
import com.charlyghislain.plancul.service.CommunicationService;
import com.charlyghislain.plancul.service.UserQueryService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class TemplateProducer {

    @Inject
    @ConfigProperty(name = ConfigConstants.APPLICATION_CONTACT_EMAIL)
    private String applicationContactMail;

    @Inject
    @ConfigProperty(name = ConfigConstants.APPLICATION_FROM_EMAIL)
    private String applicationFromEmail;

    @Inject
    @ConfigProperty(name = ConfigConstants.APPLICATION_FRONTEND_URL)
    private String applicationFrontendUrl;

    @Inject
    @ConfigProperty(name = ConfigConstants.APPLICATION_NAME)
    private String applicationName;

    @Inject
    @ConfigProperty(name = ConfigConstants.APPLICATION_PROJECT_URL)
    private String applicationProjectUrl;

    @Inject
    @Claim("uid")
    private ClaimValue<Long> authenticatorUidClaim;

    @Inject
    private CommunicationService communicationService;
    @Inject
    private UserQueryService userQueryService;

    @Produces
    @Dependent
    @ProducedTemplateContext
    public PlanculApplicationTemplate getPlanCulApplicationTemplate() {
        PlanculApplicationTemplate applicationTemplate = new PlanculApplicationTemplate();
        applicationTemplate.setContactMail(applicationContactMail);
        applicationTemplate.setFromMail(applicationFromEmail);
        applicationTemplate.setFrontendUrl(applicationFrontendUrl);
        applicationTemplate.setName(applicationName);
        applicationTemplate.setProjectUrl(applicationProjectUrl);
        return applicationTemplate;
    }


    @Produces
    @Dependent
    @ProducedTemplateContext
    public UserTemplate getLoggedPlanCulUserTemplate() {
        Long authenticatorUid = authenticatorUidClaim.getValue();
        User user = userQueryService.findUserByAuthenticatorUid(authenticatorUid)
                .orElseThrow(IllegalStateException::new);
        return communicationService.createUserTemplate(user);
    }
}
