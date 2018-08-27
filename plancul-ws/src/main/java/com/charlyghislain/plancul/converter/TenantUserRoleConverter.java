package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.api.domain.WsTenant;
import com.charlyghislain.plancul.api.domain.WsTenantRole;
import com.charlyghislain.plancul.api.domain.WsTenantUserRole;
import com.charlyghislain.plancul.api.domain.WsUser;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.TenantUserRole;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.util.UntypedSort;

import javax.inject.Inject;
import java.util.Optional;

public class TenantUserRoleConverter implements ToWsDomainObjectConverter<TenantUserRole, WsTenantUserRole> {

    @Inject
    private WsUserConverter wsUserConverter;
    @Inject
    private WsTenantConverter wsTenantConverter;

    @Override
    public WsTenantUserRole toWsEntity(TenantUserRole entity) {
        Long id = entity.getId();
        Tenant tenant = entity.getTenant();
        User user = entity.getUser();
        TenantRole tenantRole = entity.getTenantRole();

        WsRef<WsUser> userWsRef = wsUserConverter.reference(user);
        WsRef<WsTenant> tenantWsRef = wsTenantConverter.reference(tenant);
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
