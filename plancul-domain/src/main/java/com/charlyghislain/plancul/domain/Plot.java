package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.domain.util.ServiceManaged;
import com.charlyghislain.plancul.domain.util.WithAccessTimes;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
public class Plot implements DomainEntity, WithAccessTimes {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Size(max = 255)
    private String name;
    @ManyToOne
    @NotNull
    private Tenant tenant;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(@NotNull Tenant tenant) {
        this.tenant = tenant;
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
