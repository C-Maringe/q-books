package com.qbook.app.application.models.salesModels;

import lombok.Data;

import java.util.List;

@Data
public class SalesBookingOverviewModel {
    private List<SalesBookingModel> salesBookingModelList;
    private Double overviewTotal;
    private boolean cashupAlreadyStarted;
    private boolean cashupCanBeCompleted;
    private String cashupId;
}
