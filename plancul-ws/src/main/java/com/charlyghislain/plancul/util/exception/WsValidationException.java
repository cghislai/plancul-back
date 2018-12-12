package com.charlyghislain.plancul.util.exception;

import com.charlyghislain.plancul.api.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.domain.exception.ValidationViolation;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.ws.rs.core.Response;
import java.util.List;

public class WsValidationException extends WsException {

    private List<ValidationViolation> violations;
    @Nullable
    private WsDomainEntity wsDomainEntity;

    public WsValidationException(List<ValidationViolation> violations) {
        super(Response.Status.NOT_ACCEPTABLE);
        this.violations = violations;
    }

    public WsValidationException(List<ValidationViolation> violations, WsDomainEntity wsDomainEntity) {
        super(Response.Status.NOT_ACCEPTABLE);
        this.violations = violations;
        this.wsDomainEntity = wsDomainEntity;
    }

    public List<ValidationViolation> getViolations() {
        return violations;
    }

    public void setViolations(List<ValidationViolation> violations) {
        this.violations = violations;
    }

    public WsDomainEntity getWsDomainEntity() {
        return wsDomainEntity;
    }

    public void setWsDomainEntity(WsDomainEntity wsDomainEntity) {
        this.wsDomainEntity = wsDomainEntity;
    }
}
