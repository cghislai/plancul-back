package com.charlyghislain.plancul.astronomy.api.domain;

import java.time.ZonedDateTime;

public class AstronomyEvent {

    protected AstronomyEventType type;
    protected ZonedDateTime dateTime;

    public AstronomyEventType getType() {
        return type;
    }

    public void setType(AstronomyEventType type) {
        this.type = type;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
