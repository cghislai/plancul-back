package com.charlyghislain.plancul.domain.request.filter;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.User;

public class TenantUserRoleFilter {
    private Tenant tenant;
    private User user;
    private TenantRole tenantRole;

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TenantRole getTenantRole() {
        return tenantRole;
    }

    public void setTenantRole(TenantRole tenantRole) {
        this.tenantRole = tenantRole;
    }
}
