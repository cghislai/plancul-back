package com.charlyghislain.plancul.communication.message;

import com.charlyghislain.dispatcher.api.message.MessageDefinition;
import com.charlyghislain.plancul.communication.template.PlanculApplicationTemplate;

@MessageDefinition(name = "plancul-header",
        compositionItem = true,
        templateContexts = {PlanculApplicationTemplate.class})
public class PlanculHeaderMessage {
}
