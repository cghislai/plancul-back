package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.api.domain.WsUser;
import com.charlyghislain.plancul.api.domain.util.WsLanguage;
import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.util.UntypedSort;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class WsUserConverter implements ToWsDomainObjectConverter<User, WsUser> {

    @Override
    public WsUser toWsEntity(User entity) {
        String firstName = entity.getFirstName();
        String lastName = entity.getLastName();
        Language language = entity.getLanguage();

        Optional<WsLanguage> wsLanguage = WsLanguage.fromCode(language.getCode());

        WsUser wsUser = new WsUser();
        wsUser.setFirstName(firstName);
        wsUser.setLastName(lastName);
        wsUser.setLanguage(wsLanguage.orElse(WsLanguage.ENGLISH));
        return wsUser;
    }

    @Override
    public Optional<Sort<User>> mapSort(UntypedSort untypedSort) {
        return Optional.empty();
    }
}
