package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.reportingModels.*;

import java.util.List;

public interface ReportsServices {

	//Reports
	ReportBookingOverviewModel getAllBookingsDuring(Long after, Long before, String filter, String userFilter, String employeeFilter);

	ReportBookingOverviewModel getAllBookingsDuringForClientInvoice(Long after, Long before, String userFilter, String employeeFilter);

	ReportClientsOverviewModel getAllSignUpsDuring(Long after, Long before);
	
	List<ReportClientSearchModel> findClientByName(String clientName);

	List<ReportClientInsightsModel> topServiceItemsPerClient(String clientUserName);

	List<ReportTopClientModel> topClientsBetweenStartAndEnd(Long start, Long end);

	List<ReportTopTreatmentModel> topTreatmentsBetweenStartAndEnd(Long start, Long end);
}
