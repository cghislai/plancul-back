package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.domain.util.WsRef;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class WsBed implements WsDomainEntity {

    private Long id;
    @NotNull
    @Size(max = 255)
    private String name;
    @NotNull
    private WsRef<WsPlot> plotWsRef;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public WsRef<WsPlot> getPlotWsRef() {
        return plotWsRef;
    }

    public void setPlotWsRef(@NotNull WsRef<WsPlot> plotWsRef) {
        this.plotWsRef = plotWsRef;
    }
}
