package com.charlyghislain.plancul.communication.message;

import com.charlyghislain.dispatcher.api.message.MessageDefinition;
import com.charlyghislain.plancul.communication.template.PlanculApplicationTemplate;

@MessageDefinition(name = "plancul-footer",
        compositionItem = true,
        templateContexts = {PlanculApplicationTemplate.class})
public class PlanculFooterMessage {
}
