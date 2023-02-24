package com.qbook.app.application.models.reportingModels;

import lombok.Data;

import java.util.List;

@Data
public class ReportBookingOverviewModel {
    private int totalTimeWorked;
    private double totalRevenue;
    private double totalRevenueInclVAT;
    private List<ReportBookingModel> reportBookingModels;
}
