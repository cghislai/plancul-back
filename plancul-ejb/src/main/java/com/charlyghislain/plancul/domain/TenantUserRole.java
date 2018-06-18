package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.DomainEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class TenantUserRole implements DomainEntity {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @ManyToOne
    private Tenant tenant;
    @NotNull
    @ManyToOne
    private User user;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TenantRole tenantRole;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(@NotNull Tenant tenant) {
        this.tenant = tenant;
    }

    @NotNull
    public User getUser() {
        return user;
    }

    public void setUser(@NotNull User user) {
        this.user = user;
    }

    @NotNull
    public TenantRole getTenantRole() {
        return tenantRole;
    }

    public void setTenantRole(@NotNull TenantRole tenantRole) {
        this.tenantRole = tenantRole;
    }
}
