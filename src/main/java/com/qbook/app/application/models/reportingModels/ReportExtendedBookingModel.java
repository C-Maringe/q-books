package com.qbook.app.application.models.reportingModels;

import lombok.Data;

@Data
public class ReportExtendedBookingModel extends ReportBookingModel {
    // cash up details
    private double cashTotal;
    private double cardPaymentTotal;
    private double eftTotal;
    private double otherTotal;
    private boolean depositPaid;
    private double depositAmount;
}
