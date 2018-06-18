package com.charlyghislain.plancul.converter.request;

import com.charlyghislain.plancul.converter.TenantConverter;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.WsTenant;
import com.charlyghislain.plancul.domain.WsTenantRole;
import com.charlyghislain.plancul.domain.request.UserCreationRequest;
import com.charlyghislain.plancul.domain.request.WsUserCreationRequest;
import com.charlyghislain.plancul.domain.request.WsUserTenantCreationRequest;

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
        String password = wsUserCreationRequest.getPassword();
        WsTenantRole tenantRole = wsUserCreationRequest.getTenantRole();

        TenantRole tenantRoleValue = TenantRole.valueOf(tenantRole.name());

        UserCreationRequest userCreationRequest = new UserCreationRequest();
        userCreationRequest.setEmail(email);
        userCreationRequest.setFirstName(firstName);
        userCreationRequest.setLastName(lastName);
        userCreationRequest.setPassword(password);
        userCreationRequest.setTenant(tenant);
        userCreationRequest.setTenantRole(tenantRoleValue);
        return userCreationRequest;
    }


    public UserCreationRequest fromWsUserTenantCreationRequest(WsUserTenantCreationRequest wsUserCreationRequest) {
        String firstName = wsUserCreationRequest.getFirstName();
        String lastName = wsUserCreationRequest.getLastName();
        String email = wsUserCreationRequest.getEmail();
        String password = wsUserCreationRequest.getPassword();
        WsTenant tenant = wsUserCreationRequest.getTenant();
        WsTenantRole tenantRole = wsUserCreationRequest.getTenantRole();

        Tenant tenantValue = tenantConverter.fromWsEntity(tenant);
        TenantRole tenantRoleValue = TenantRole.valueOf(tenantRole.name());

        UserCreationRequest userCreationRequest = new UserCreationRequest();
        userCreationRequest.setEmail(email);
        userCreationRequest.setFirstName(firstName);
        userCreationRequest.setLastName(lastName);
        userCreationRequest.setPassword(password);
        userCreationRequest.setTenant(tenantValue);
        userCreationRequest.setTenantRole(tenantRoleValue);
        return userCreationRequest;
    }
}