package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.domain.util.ServiceManaged;
import com.charlyghislain.plancul.domain.util.WithAccessTimes;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class TenantUserRoleInvitation implements DomainEntity, WithAccessTimes {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @ManyToOne
    private Tenant tenant;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TenantRole tenantRole;
    @NotNull
    private String token;

    @NotNull
    @ServiceManaged
    private LocalDateTime created = LocalDateTime.now();
    @NotNull
    @ServiceManaged
    private LocalDateTime updated = LocalDateTime.now();

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
    public TenantRole getTenantRole() {
        return tenantRole;
    }

    public void setTenantRole(@NotNull TenantRole tenantRole) {
        this.tenantRole = tenantRole;
    }

    @NotNull
    public String getToken() {
        return token;
    }

    public void setToken(@NotNull String token) {
        this.token = token;
    }


    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }
}
