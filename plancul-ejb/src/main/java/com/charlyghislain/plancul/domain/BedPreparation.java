package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.domain.util.ServiceManaged;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
public class BedPreparation implements DomainEntity {

    @Id
    @GeneratedValue
    private Long id;
    @Min(1)
    private int dayDuration;
    @NotNull
    private BedPreparationType type;
    @NotNull
    @ServiceManaged
    private LocalDate startDate;
    @NotNull
    @ServiceManaged
    private LocalDate endDate;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDayDuration() {
        return dayDuration;
    }

    public void setDayDuration(int dayDuration) {
        this.dayDuration = dayDuration;
    }

    @NotNull
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull LocalDate startDate) {
        this.startDate = startDate;
    }

    @NotNull
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull LocalDate endDate) {
        this.endDate = endDate;
    }

    @NotNull
    public BedPreparationType getType() {
        return type;
    }

    public void setType(@NotNull BedPreparationType type) {
        this.type = type;
    }
}
