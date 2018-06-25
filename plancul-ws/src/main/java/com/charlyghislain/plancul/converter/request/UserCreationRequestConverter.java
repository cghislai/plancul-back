package com.charlyghislain.plancul.converter.request;

import com.charlyghislain.plancul.converter.TenantConverter;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.api.WsTenant;
import com.charlyghislain.plancul.domain.api.WsTenantRole;
import com.charlyghislain.plancul.domain.api.request.WsUserCreationRequest;
import com.charlyghislain.plancul.domain.api.request.WsUserTenantCreationRequest;
import com.charlyghislain.plancul.domain.api.util.WsLanguage;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.UserCreationRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class UserCreationRequestConverter {

    @Inject
    private TenantConverter tenantConverter;

    public UserCreationRequest fromWsUserCreationRequest(WsUserCreationRequest wsUserCreationRequest, Tenant tenant) {
        String firstName = wsUserCreationRequest.getFirstName();
        String lastName = wsUserCreationRequest.getLastName();
        String email = wsUserCreationRequest.getEmail();
        WsTenantRole tenantRole = wsUserCreationRequest.getTenantRole();
        WsLanguage wsLanguage = wsUserCreationRequest.getLanguage();

        TenantRole tenantRoleValue = TenantRole.valueOf(tenantRole.name());
        Language language = Language.fromCode(wsLanguage.getCode())
                .orElseThrow(IllegalStateException::new);

        UserCreationRequest userCreationRequest = new UserCreationRequest();
        userCreationRequest.setEmail(email);
        userCreationRequest.setFirstName(firstName);
        userCreationRequest.setLastName(lastName);
        userCreationRequest.setLanguage(language);
        userCreationRequest.setTenant(tenant);
        userCreationRequest.setTenantRole(tenantRoleValue);
        return userCreationRequest;
    }


    public UserCreationRequest fromWsUserTenantCreationRequest(WsUserTenantCreationRequest wsUserCreationRequest) {
        String firstName = wsUserCreationRequest.getFirstName();
        String lastName = wsUserCreationRequest.getLastName();
        String email = wsUserCreationRequest.getEmail();
        WsTenant tenant = wsUserCreationRequest.getTenant();
        WsLanguage wsLanguage = wsUserCreationRequest.getLanguage();

        Language language = Language.fromCode(wsLanguage.getCode())
                .orElseThrow(IllegalStateException::new);
        Tenant tenantValue = tenantConverter.fromWsEntity(tenant);

        UserCreationRequest userCreationRequest = new UserCreationRequest();
        userCreationRequest.setEmail(email);
        userCreationRequest.setFirstName(firstName);
        userCreationRequest.setLastName(lastName);
        userCreationRequest.setLanguage(language);
        userCreationRequest.setTenant(tenantValue);
        return userCreationRequest;
    }
}
