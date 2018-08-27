package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.domain.BedPreparation;
import com.charlyghislain.plancul.domain.BedPreparationType;
import com.charlyghislain.plancul.api.domain.WsBedPreparation;
import com.charlyghislain.plancul.api.domain.WsBedPreparationType;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;

@ApplicationScoped
public class BedPreparationConverter {

    public WsBedPreparation toWsEntity(BedPreparation entity) {
        Long id = entity.getId();
        int dayDuration = entity.getDayDuration();
        LocalDate startdate = entity.getStartDate();
        LocalDate endDate = entity.getEndDate();
        BedPreparationType type = entity.getType();

        WsBedPreparationType wsBedPreparationType = WsBedPreparationType.valueOf(type.name());

        WsBedPreparation wsBedPreparation = new WsBedPreparation();
        wsBedPreparation.setId(id);
        wsBedPreparation.setDayDuration(dayDuration);
        wsBedPreparation.setStartDate(startdate);
        wsBedPreparation.setEndDate(endDate);
        wsBedPreparation.setType(wsBedPreparationType);
        return wsBedPreparation;
    }

    public BedPreparation fromWsEntity(WsBedPreparation wsEntity) {
        Long id = wsEntity.getId();

        BedPreparation bedPreparation = new BedPreparation();
        bedPreparation.setId(id);

        updateEntity(bedPreparation, wsEntity);
        return bedPreparation;
    }

    public void updateEntity(BedPreparation entity, WsBedPreparation wsEntity) {
        int dayDuration = wsEntity.getDayDuration();
        LocalDate startdate = wsEntity.getStartDate();
        LocalDate endDate = wsEntity.getEndDate();
        WsBedPreparationType wsBedPreparationType = wsEntity.getType();

        BedPreparationType bedPreparationType = BedPreparationType.valueOf(wsBedPreparationType.name());

        entity.setDayDuration(dayDuration);
        entity.setStartDate(startdate);
        entity.setEndDate(endDate);
        entity.setType(bedPreparationType);
    }

}
