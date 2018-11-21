package com.charlyghislain.plancul.astronomy.cache.domain;

import com.charlyghislain.plancul.astronomy.api.domain.Zodiac;

import javax.persistence.*;


@Entity
@DiscriminatorValue("MOON_ZODIAK_CHANGE")
public class CachedMoonZodiakChangeEvent extends CachedAstronomyEvent {

    @Column(name = "ZODIAK")
    @Enumerated(EnumType.STRING)
    private Zodiac zodiac;

    public Zodiac getZodiac() {
        return zodiac;
    }

    public void setZodiac(Zodiac zodiac) {
        this.zodiac = zodiac;
    }
}
