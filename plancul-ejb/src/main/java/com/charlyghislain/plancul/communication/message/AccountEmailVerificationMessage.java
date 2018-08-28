package com.charlyghislain.plancul.communication.message;

import com.charlyghislain.dispatcher.api.header.MailHeaders;
import com.charlyghislain.dispatcher.api.message.MessageDefinition;
import com.charlyghislain.plancul.communication.template.EmailVerificationTemplate;
import com.charlyghislain.plancul.communication.template.PlanculApplicationTemplate;
import com.charlyghislain.plancul.communication.template.UserTemplate;

@MessageDefinition(name = "account-email-verification",
        header = PlanculHeaderMessage.class,
        footer = PlanculFooterMessage.class,
        templateContexts = {UserTemplate.class, EmailVerificationTemplate.class, PlanculApplicationTemplate.class}
)
@MailHeaders(from = "${app.fromMail}", to = "${user.email}")
public class AccountEmailVerificationMessage {
}
