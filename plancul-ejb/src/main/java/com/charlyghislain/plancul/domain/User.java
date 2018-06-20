package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.security.Caller;
import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.validation.ValidEmail;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "PLANCULUSER")
public class User implements DomainEntity {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Caller caller;
    @NotNull
    @Size(max = 255)
    private String firstName;
    @NotNull
    @Size(max = 255)
    private String lastName;
    @NotNull
    @Size(max = 255)
    @ValidEmail
    private String email;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Language language;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Caller getCaller() {
        return caller;
    }

    public void setCaller(Caller caller) {
        this.caller = caller;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotNull
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(@NotNull Language language) {
        this.language = language;
    }
}
