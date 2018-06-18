package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.LocalizedMessage;
import com.charlyghislain.plancul.domain.i18n.WsLocalizedMessage;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocalizedMessageConverter implements ToWsDomainObjectConverter<LocalizedMessage, WsLocalizedMessage> {

    @Override
    public WsLocalizedMessage toWsEntity(LocalizedMessage entity) {
        Long id = entity.getId();
        String label = entity.getLabel();
        Language language = entity.getLanguage();

        String code = language.getCode();

        WsLocalizedMessage wsLocalizedMessage = new WsLocalizedMessage();
        wsLocalizedMessage.setId(id);
        wsLocalizedMessage.setLabel(label);
        wsLocalizedMessage.setLanguageCode(code);
        return wsLocalizedMessage;
    }
}
