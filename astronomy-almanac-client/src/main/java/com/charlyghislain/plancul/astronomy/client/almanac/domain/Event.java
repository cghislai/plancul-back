package com.charlyghislain.plancul.astronomy.client.almanac.domain;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Event implements Serializable {

    @JsonbProperty("body")
    private String body;
    @JsonbProperty("time_utc")
    private LocalDateTime dateTime;
    @JsonbProperty("type")
    private String eventType;
    @JsonbProperty("index")
    private Integer indexValue;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Integer indexValue) {
        this.indexValue = indexValue;
    }
}
