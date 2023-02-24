package com.qbook.app.application.models.reportingModels;

import lombok.Data;

@Data
public class ReportBookingModel {
    private String clientId;
    private String clientFullName;
    private String clientEmail;
    private String bookingSlot;
    private String treatments;
    private int totalTimeSpentOnBooking;
    private double totalRevenueForBooking;
    private double totalRevenueForBookingIncludingVAT;
    private String employeeFullName;
    private double depositAmount;
    private boolean depositPaid;
}
