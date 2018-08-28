package com.charlyghislain.plancul.communication.message;

import com.charlyghislain.dispatcher.api.header.MailHeaders;
import com.charlyghislain.dispatcher.api.message.MessageDefinition;
import com.charlyghislain.plancul.communication.template.PasswordResetTemplate;
import com.charlyghislain.plancul.communication.template.PlanculApplicationTemplate;
import com.charlyghislain.plancul.communication.template.UserTemplate;

@MessageDefinition(name = "account-password-reset",
        header = PlanculHeaderMessage.class,
        footer = PlanculFooterMessage.class,
        templateContexts = {UserTemplate.class, PasswordResetTemplate.class, PlanculApplicationTemplate.class}
)
@MailHeaders(from = "${app.fromMail}", to = "${user.email}")
public class AccountPasswordResetMessage {
}
