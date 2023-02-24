package com.qbook.app.application.models.reportingModels;

import lombok.Data;

import java.util.List;

@Data
public class ReportClientsOverviewModel {
    private int totalActiveClients;
    private int totalClientsWithBookings;
    private List<ReportClientSignUpModel> reportClientSignUpModels;
}
