package com.charlyghislain.plancul.domain.api.result;

import com.charlyghislain.plancul.domain.api.util.WsDomainEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WsValidationError implements Serializable {

    private List<WsContraintViolation> errors = new ArrayList<>();
    private WsDomainEntity entity;

    public List<WsContraintViolation> getErrors() {
        return errors;
    }

    public void setErrors(List<WsContraintViolation> errors) {
        this.errors = errors;
    }

    public WsDomainEntity getEntity() {
        return entity;
    }

    public void setEntity(WsDomainEntity entity) {
        this.entity = entity;
    }
}
