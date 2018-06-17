package com.charlyghislain.plancul.opendata.agrovoc.domain;

public class AgrovocPlantProductResultTuple {

    private AgrovocPlantResult plant;
    private AgrovocProductResult product;

    public AgrovocPlantResult getPlant() {
        return plant;
    }

    public void setPlant(AgrovocPlantResult plant) {
        this.plant = plant;
    }

    public AgrovocProductResult getProduct() {
        return product;
    }

    public void setProduct(AgrovocProductResult product) {
        this.product = product;
    }
}
