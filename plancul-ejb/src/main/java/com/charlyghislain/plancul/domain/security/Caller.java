package com.charlyghislain.plancul.domain.security;

import com.charlyghislain.plancul.domain.util.DomainEntity;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
public class Caller implements DomainEntity {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Size(max = 255)
    private String name;
    @Nullable
    @Size(max = 400)
    private String password;
    private boolean passwordNeedsChange;
    @Nullable
    @Size(max = 255)
    private String passwordResetToken;
    @Nullable
    private LocalDateTime passwordResetTokenExpiration;
    private boolean active;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPasswordNeedsChange() {
        return passwordNeedsChange;
    }

    public void setPasswordNeedsChange(boolean passwordExpired) {
        this.passwordNeedsChange = passwordExpired;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public LocalDateTime getPasswordResetTokenExpiration() {
        return passwordResetTokenExpiration;
    }

    public void setPasswordResetTokenExpiration(LocalDateTime passwordResetTokenExpiration) {
        this.passwordResetTokenExpiration = passwordResetTokenExpiration;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
