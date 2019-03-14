package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.domain.util.ServiceManaged;
import com.charlyghislain.plancul.domain.util.WithAccessTimes;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Crop implements DomainEntity, WithAccessTimes {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Size(max = 255)
    private String family;
    @NotNull
    @Size(max = 255)
    private String species;

    @NotNull
    @JoinTable(name = "CROP_DISPLAYNAME",
            joinColumns = @JoinColumn(name = "CROP_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "LOCALIZEDMESSAGE_ID", referencedColumnName = "ID"))
    @OneToMany(cascade = CascadeType.ALL)
    private List<LocalizedMessage> displayName = new ArrayList<>();


    @NotNull
    @ManyToOne
    private User creationUser;
    @NotNull
    @ServiceManaged
    private LocalDateTime created = LocalDateTime.now();
    @NotNull
    @ServiceManaged
    private LocalDateTime updated = LocalDateTime.now();

    @Nullable
    @Size(max = 255)
    private String subSpecies;
    @Nullable
    @Size(max = 255)
    private String cultivar;

    @Nullable
    @ManyToOne
    private AgrovocPlant agrovocPlant;
    @Nullable
    @ManyToOne
    private AgrovocProduct agrovocProduct;
    /**
     * An optional tenant this crop would be restricted to
     */
    @Nullable
    @ManyToOne
    private Tenant tenant;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getFamily() {
        return family;
    }

    public void setFamily(@NotNull String family) {
        this.family = family;
    }

    @NotNull
    public String getSpecies() {
        return species;
    }

    public void setSpecies(@NotNull String species) {
        this.species = species;
    }

    public Optional<String> getSubSpecies() {
        return Optional.ofNullable(subSpecies);
    }

    public void setSubSpecies(String subSpecies) {
        this.subSpecies = subSpecies;
    }

    public Optional<String> getCultivar() {
        return Optional.ofNullable(cultivar);
    }

    public void setCultivar(String cultivar) {
        this.cultivar = cultivar;
    }

    public Optional<AgrovocPlant> getAgrovocPlant() {
        return Optional.ofNullable(agrovocPlant);
    }

    public void setAgrovocPlant(AgrovocPlant agrovocPlant) {
        this.agrovocPlant = agrovocPlant;
    }

    public Optional<AgrovocProduct> getAgrovocProduct() {
        return Optional.ofNullable(agrovocProduct);
    }

    public void setAgrovocProduct(AgrovocProduct agrovocProduct) {
        this.agrovocProduct = agrovocProduct;
    }

    public Optional<Tenant> getTenant() {
        return Optional.ofNullable(tenant);
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @NotNull
    public List<LocalizedMessage> getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@NotNull List<LocalizedMessage> displayName) {
        this.displayName = displayName;
    }

    @NotNull
    public User getCreationUser() {
        return creationUser;
    }

    public void setCreationUser(@NotNull User creationUser) {
        this.creationUser = creationUser;
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
