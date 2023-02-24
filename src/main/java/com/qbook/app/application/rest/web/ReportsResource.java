package com.qbook.app.application.rest.web;

import com.qbook.app.application.models.ViewClientModel;
import com.qbook.app.application.models.clientModels.ClientDisabledModel;
import com.qbook.app.application.models.clientModels.ClientEnabledModel;
import com.qbook.app.application.models.employeeModels.ViewEmployeeModel;
import com.qbook.app.application.models.reportingModels.*;
import com.qbook.app.application.services.appservices.ClientServices;
import com.qbook.app.application.services.appservices.EmailService;
import com.qbook.app.application.services.appservices.ReportsServices;
import com.qbook.app.application.services.appservices.SuperUserServices;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
@RequestMapping("/api/auth/reporting")
@AllArgsConstructor
public class ReportsResource {

    private final ReportsServices reportsServices;
	private final ClientServices clientServices;
	private final SuperUserServices superUserServices;

    @GetMapping("search")
    public ResponseEntity<List<ReportClientSearchModel>> searchName(@RequestParam(value = "query") String query){
	    return new ResponseEntity<>(reportsServices.findClientByName(query), HttpStatus.OK);
    }

    @GetMapping("bookings/{from}/{to}/{bookingStatusFilter}/{userFilter}/{employeeFilter}")
    public ResponseEntity<ReportBookingOverviewModel> searchBookings(@PathVariable("from") String from,
                                   @PathVariable("to") String to,
                                   @PathVariable("bookingStatusFilter") String bookingStatusFilter,
                                   @PathVariable("userFilter") String userFilter,
                                   @PathVariable("employeeFilter") String employeeFilter){
        DateTime fromDate = new DateTime(from);
        DateTime toDate = new DateTime(to);
        return new ResponseEntity<>(reportsServices.getAllBookingsDuring(fromDate.getMillis(), toDate.getMillis(), bookingStatusFilter, userFilter, employeeFilter), HttpStatus.OK);
    }

    @GetMapping("signups/{from}/{to}")
    public ResponseEntity<ReportClientsOverviewModel> searchClients(@PathVariable("from") String from, @PathVariable("to") String to){
        DateTime fromDate = new DateTime(from);
        DateTime toDate = new DateTime(to);
        return new ResponseEntity<>(reportsServices.getAllSignUpsDuring(fromDate.getMillis(), toDate.getMillis()), HttpStatus.OK);
    }

	@GetMapping("getTopServiceItemsPerClient/{clientUsername}")
	public ResponseEntity<List<ReportClientInsightsModel>> getTopServiceItemsPerClient(@PathVariable("clientUsername") String username){
		return new ResponseEntity<>(reportsServices.topServiceItemsPerClient(username), HttpStatus.OK);
	}

	@PutMapping("client/disable/{clientId}")
	public ResponseEntity<ClientDisabledModel> disableAccount(@PathVariable("clientId") String clientId){
		return new ResponseEntity<>(clientServices.disableAccount(clientId), HttpStatus.OK);
	}

	@PutMapping("client/enable/{clientId}")
	public ResponseEntity<ClientEnabledModel> enableAccount(@PathVariable("clientId") String clientId){
		return new ResponseEntity<>(clientServices.enableAccount(clientId), HttpStatus.OK);
	}

	@GetMapping("employeesFilter")
	public ResponseEntity<List<ViewEmployeeModel>> viewAllEmployeesForFilter(){
		return new ResponseEntity<>(superUserServices.getAllEmployees(), HttpStatus.OK);
	}

	@GetMapping("clientsFilter")
	public ResponseEntity<List<ViewClientModel>> viewAllClientsForFilter(){
		return new ResponseEntity<>(clientServices.getAllClients(), HttpStatus.OK);
	}

	//	TODO: Rewrite
//	@PutMapping("client/email")
//	public ResponseEntity<ReportClientEmailSentModel> sendClientQuickEmail(ReportClientEmailModel reportClientEmailModel){
//        Client client = clientCrudService.getEntityBy(reportClientEmailModel.getClientId());
//        emailService.sendEmail(client, reportClientEmailModel.getTitle(), reportClientEmailModel.getMessage());
//
//        ReportClientEmailSentModel reportClientEmailSentModel = new ReportClientEmailSentModel();
//        reportClientEmailSentModel.setMessage("The email was sent successfully");
//
//		return new ResponseEntity<>(reportClientEmailSentModel, HttpStatus.OK);
//	}

//	TODO: Rewrite
//	@PutMapping("client/invoice/email")
//	public ResponseEntity<ReportClientEmailInvoiceSentModel> sendClientInvoiceEmail(@RequestBody ReportClientEmailInvoiceModel reportClientEmailInvoiceModel){
//        Client client = clientCrudService.getEntityByEmail(reportClientEmailInvoiceModel.getClientEmail());
//
//        if(client == null) {
//            throw new InvalidClientException("The client provided could not be found please provide a valid client email.");
//        } else {
//            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd, MMM yyyy");
//            DateTime fromDate = new DateTime(reportClientEmailInvoiceModel.getFrom());
//            DateTime toDate = new DateTime(reportClientEmailInvoiceModel.getTo());
//
//            ReportBookingOverviewModel reportBookingOverviewModel = reportsServices
//                    .getAllBookingsDuringForClientInvoice(
//                            fromDate.getMillis(),
//                            toDate.getMillis(),
//                            reportClientEmailInvoiceModel.getClientEmail(),
//                            reportClientEmailInvoiceModel.getEmployeeEmail()
//                    );
//
//            emailService.sendClientBookingInvoiceEmailForPeriod(client.getUsername(),
//                    client.getFirstName() + " " + client.getLastName(),
//                    fromDate.toString(dateTimeFormatter),
//                    toDate.toString(dateTimeFormatter),
//                    reportBookingOverviewModel);
//            ReportClientEmailInvoiceSentModel reportClientEmailInvoiceSentModel = new ReportClientEmailInvoiceSentModel();
//            reportClientEmailInvoiceSentModel.setMessage("The clients invoice was sent successfully");
//            Response.ResponseBuilder builder = Response
//                    .ok(reportClientEmailInvoiceSentModel)
//                    .type(MediaType.APPLICATION_JSON_TYPE);
//
//            return builder.build();
//        }
//	}

	@GetMapping("topBookedServiceItem/{start}/{end}")
	public ResponseEntity<List<ReportTopTreatmentModel>> getTopBookedServiceItem(@PathVariable("start") Long start, @PathVariable("end") Long end) {
		return new ResponseEntity<>(reportsServices.topTreatmentsBetweenStartAndEnd(start, end), HttpStatus.OK);
	}

	@GetMapping("topBookedClient/{start}/{end}")
	public ResponseEntity<List<ReportTopClientModel>> getTopBookedClients(@PathVariable("start") Long start, @PathVariable("end") Long end) {
		return new ResponseEntity<>(reportsServices.topClientsBetweenStartAndEnd(start, end), HttpStatus.OK);
	}
}
