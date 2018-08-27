package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.domain.TenantRole;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class TenantRoleConverter {

    public Optional<TenantRole> toTenantRole(String tenantRoleName) {
        try {
            return Optional.of(TenantRole.valueOf(tenantRoleName));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
