package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.DomainEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AgrovocProduct implements DomainEntity {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private String agrovocNodeId;
    @NotNull
    @JoinTable(name = "AGROVOCPRODUCT_PREFLABEL",
            joinColumns = @JoinColumn(name = "AGROVOCPRODUCT_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "LOCALIZEDMESSAGE_ID", referencedColumnName = "ID"))
    @ManyToMany
    private List<LocalizedMessage> preferedLabel = new ArrayList<>();
    @NotNull
    @JoinTable(name = "AGROVOCPRODUCT_ALTLABEL",
            joinColumns = @JoinColumn(name = "AGROVOCPRODUCT_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "LOCALIZEDMESSAGE_ID", referencedColumnName = "ID"))
    @ManyToMany
    private List<LocalizedMessage> alternativeLabels = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getAgrovocNodeId() {
        return agrovocNodeId;
    }

    public void setAgrovocNodeId(@NotNull String agrovocNodeId) {
        this.agrovocNodeId = agrovocNodeId;
    }

    @NotNull
    public List<LocalizedMessage> getPreferedLabel() {
        return preferedLabel;
    }

    public void setPreferedLabel(@NotNull List<LocalizedMessage> preferedLabel) {
        this.preferedLabel = preferedLabel;
    }

    @NotNull
    public List<LocalizedMessage> getAlternativeLabels() {
        return alternativeLabels;
    }

    public void setAlternativeLabels(@NotNull List<LocalizedMessage> alternativeLabels) {
        this.alternativeLabels = alternativeLabels;
    }
}
