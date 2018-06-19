package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.TenantUserRole;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.WsTenant;
import com.charlyghislain.plancul.domain.WsTenantRole;
import com.charlyghislain.plancul.domain.WsUser;
import com.charlyghislain.plancul.domain.WsTenantUserRole;
import com.charlyghislain.plancul.domain.util.WsRef;

import javax.inject.Inject;

public class TenantUserRoleConverter implements ToWsDomainObjectConverter<TenantUserRole, WsTenantUserRole> {

    @Inject
    private TenantConverter tenantConverter;
    @Inject
    private UserConverter userConverter;

    @Override
    public WsTenantUserRole toWsEntity(TenantUserRole entity) {
        Long id = entity.getId();
        Tenant tenant = entity.getTenant();
        User user = entity.getUser();
        TenantRole tenantRole = entity.getTenantRole();

        WsRef<WsUser> userWsRef = userConverter.reference(user);
        WsRef<WsTenant> tenantWsRef = tenantConverter.reference(tenant);
        WsTenantRole wsTenantRole = WsTenantRole.valueOf(tenantRole.name());

        WsTenantUserRole wsTenantUserRole = new WsTenantUserRole();
        wsTenantUserRole.setId(id);
        wsTenantUserRole.setRole(wsTenantRole);
        wsTenantUserRole.setTenantWsRef(tenantWsRef);
        wsTenantUserRole.setUserWsRef(userWsRef);
        return wsTenantUserRole;
    }
}
