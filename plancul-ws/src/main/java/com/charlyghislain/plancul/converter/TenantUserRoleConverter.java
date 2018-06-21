package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.TenantUserRole;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.api.WsTenant;
import com.charlyghislain.plancul.domain.api.WsTenantRole;
import com.charlyghislain.plancul.domain.api.WsTenantUserRole;
import com.charlyghislain.plancul.domain.api.WsUser;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.api.util.WsRef;
import com.charlyghislain.plancul.util.UntypedSort;

import javax.inject.Inject;
import java.util.Optional;

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

    @Override
    public Optional<Sort<TenantUserRole>> mapSort(UntypedSort untypedSort) {
        return Optional.empty();
    }
}
