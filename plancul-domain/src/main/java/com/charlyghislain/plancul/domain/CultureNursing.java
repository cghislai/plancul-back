package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.domain.util.ServiceManaged;
import com.charlyghislain.plancul.domain.util.WithAccessTimes;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class CultureNursing implements DomainEntity, WithAccessTimes {

    @Id
    @GeneratedValue
    private Long id;
    @Min(1)
    private int dayDuration;
    @NotNull
    @ServiceManaged
    private LocalDate startDate;
    @NotNull
    @ServiceManaged
    private LocalDate endDate;

    @NotNull
    @ServiceManaged
    private LocalDateTime created = LocalDateTime.now();
    @NotNull
    @ServiceManaged
    private LocalDateTime updated = LocalDateTime.now();

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

    public void setStartDate(@NotNull LocalDate startdate) {
        this.startDate = startdate;
    }

    @NotNull
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull LocalDate endDate) {
        this.endDate = endDate;
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
