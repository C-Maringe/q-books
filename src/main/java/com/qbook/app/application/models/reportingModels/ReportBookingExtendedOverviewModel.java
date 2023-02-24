package com.qbook.app.application.models.reportingModels;

import lombok.Data;

import java.util.List;

@Data
public class ReportBookingExtendedOverviewModel extends ReportBookingOverviewModel {
    private List<ReportExtendedBookingModel> reportExtendedBookingModels;
}
