package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.api.domain.WsUser;
import com.charlyghislain.plancul.api.domain.util.WsLanguage;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import com.charlyghislain.plancul.converter.util.FromWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.service.UserQueryService;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class UserConverter implements FromWsDomainObjectConverter<User, WsUser> {

    @Inject
    private UserQueryService userQueryService;

    public User load(WsRef<WsUser> ref) {
        return userQueryService.findUserById(ref.getId())
                .orElseThrow(ReferenceNotFoundException::new);
    }

    @Override
    public User fromWsEntity(WsUser wsEntity) {
        String firstName = wsEntity.getFirstName();
        String lastName = wsEntity.getLastName();
        WsLanguage wsLanguage = wsEntity.getLanguage();

        Language language = Language.fromCode(wsLanguage.getCode())
                .orElse(Language.DEFAULT_LANGUAGE);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setLanguage(language);
        return user;
    }


}
