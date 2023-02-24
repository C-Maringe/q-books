package com.qbook.app.application.models.salesModels;

import lombok.Data;

@Data
public class SalesBookingItemModel {
    private String treatmentId;
    private String treatmentName;
    private String treatmentDescription;
    private double treatmentPrice;
    private int treatmentDuration;
    private int quantity;
    private boolean special;
    private double specialPrice;

    public SalesBookingItemModel(String treatmentId, String treatmentName, String treatmentDescription, double treatmentPrice, int treatmentDuration, int quantity, boolean special, double specialPrice) {
        this.treatmentId = treatmentId;
        this.treatmentName = treatmentName;
        this.treatmentDescription = treatmentDescription;
        this.treatmentPrice = treatmentPrice;
        this.treatmentDuration = treatmentDuration;
        this.quantity = quantity;
        this.special = special;
        this.specialPrice = specialPrice;
    }
}
