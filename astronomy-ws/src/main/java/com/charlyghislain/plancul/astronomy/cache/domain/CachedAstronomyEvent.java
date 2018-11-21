package com.charlyghislain.plancul.astronomy.cache.domain;

import com.charlyghislain.plancul.astronomy.api.domain.AstronomyEventType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static javax.persistence.InheritanceType.SINGLE_TABLE;

@Entity
@Table(name = "CACHED_EVENT")
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "EVENTTYPE")
public class CachedAstronomyEvent {

    @Id
    @GeneratedValue
    protected Long Id;
    @Column(name = "EVENTTYPE")
    @Enumerated(EnumType.STRING)
    @NotNull
    private AstronomyEventType eventType;
    @Column(name = "DATETIME")
    @NotNull
    private LocalDateTime localDateTimeUTC;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public AstronomyEventType getEventType() {
        return eventType;
    }

    public void setEventType(AstronomyEventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getLocalDateTimeUTC() {
        return localDateTimeUTC;
    }

    public void setLocalDateTimeUTC(LocalDateTime localDateTimeUTC) {
        this.localDateTimeUTC = localDateTimeUTC;
    }
}
