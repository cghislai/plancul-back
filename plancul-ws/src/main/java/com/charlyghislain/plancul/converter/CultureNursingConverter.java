package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.domain.CultureNursing;
import com.charlyghislain.plancul.domain.WsCultureNursing;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;

@ApplicationScoped
public class CultureNursingConverter {

    public WsCultureNursing toWsEntity(CultureNursing entity) {
        Long id = entity.getId();
        int dayDuration = entity.getDayDuration();
        LocalDate startdate = entity.getStartdate();
        LocalDate endDate = entity.getEndDate();

        WsCultureNursing wsCultureNursing = new WsCultureNursing();
        wsCultureNursing.setId(id);
        wsCultureNursing.setDayDuration(dayDuration);
        wsCultureNursing.setStartdate(startdate);
        wsCultureNursing.setEndDate(endDate);
        return wsCultureNursing;
    }

    public CultureNursing fromWsEntity(WsCultureNursing wsEntity) {
        Long id = wsEntity.getId();

        CultureNursing cultureNursing = new CultureNursing();
        cultureNursing.setId(id);

        updateEntity(cultureNursing, wsEntity);
        return cultureNursing;
    }

    public void updateEntity(CultureNursing entity, WsCultureNursing wsEntity) {
        int dayDuration = wsEntity.getDayDuration();
        LocalDate startdate = wsEntity.getStartdate();
        LocalDate endDate = wsEntity.getEndDate();


        entity.setDayDuration(dayDuration);
        entity.setStartdate(startdate);
        entity.setEndDate(endDate);
    }

}