package com.qbook.app.application.models.reportingModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportTopClientModel {
    private String clientName;
    private int totalBookings;
}
