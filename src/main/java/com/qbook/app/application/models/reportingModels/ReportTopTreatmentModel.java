package com.qbook.app.application.models.reportingModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportTopTreatmentModel {
    private String treatmentName;
    private int totalBookings;
}
