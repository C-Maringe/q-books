package com.qbook.app.application.rest.web;

import com.qbook.app.application.models.BookingCancellationModel;
import com.qbook.app.application.models.BookingCreatedModel;
import com.qbook.app.application.models.TimeSlotModel;
import com.qbook.app.application.models.employeeModels.EmployeeScheduleModel;
import com.qbook.app.application.models.productModels.ViewProductModel;
import com.qbook.app.application.models.salesModels.*;
import com.qbook.app.application.models.scheduleModels.ScheduleClientModel;
import com.qbook.app.application.models.scheduleModels.ScheduleTreatmentModel;
import com.qbook.app.application.services.appservices.AuthTokenServices;
import com.qbook.app.application.models.productModels.ProductItemToCaptureModel;
import com.qbook.app.application.services.appservices.ProductServices;
import com.qbook.app.application.services.appservices.SalesServices;
import com.qbook.app.application.services.appservices.ScheduleServices;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
@RequestMapping("/api/auth/sales")
@AllArgsConstructor
public class SalesResource {
	private final SalesServices salesServices;
	private final AuthTokenServices authTokenServices;
	private final ScheduleServices scheduleServices;
	private final ProductServices productServices;

	@PutMapping("{bookingId}/cancel")
	public ResponseEntity<BookingCancellationModel> cancelBooking
            (@RequestHeader("Authorization") String Authorization, @PathVariable("bookingId") String bookingId) {
		return new ResponseEntity<>(salesServices.cancelBooking(new ObjectId(bookingId), new ObjectId(authTokenServices.extractUserId(Authorization))), HttpStatus.OK);
	}

	@PutMapping("cashup-start/{date}")
	public ResponseEntity<SalesBookingOverviewModel> startCashupForDate(@RequestHeader("Authorization") String authorization, @PathVariable("date") String date) {
		return new ResponseEntity<>(salesServices.startCashingUpSpecificDay(authTokenServices.extractUserId(authorization), date), HttpStatus.OK);
	}

	@GetMapping("/view/{id}")
	public ResponseEntity<SaleSpecificBookingModel> viewSpecificSaleToUpdate(@PathVariable("id") String id) {
		return new ResponseEntity<>(salesServices.viewSpecificSaleToUpdate(id), HttpStatus.OK);
	}

	@GetMapping("employees")
	public ResponseEntity<List<EmployeeScheduleModel>> getAllActiveEmployees() {
		return new ResponseEntity<>(scheduleServices.getAllActiveEmployeesForSchedule(), HttpStatus.OK);
	}

	@GetMapping("times")
	public ResponseEntity<List<TimeSlotModel>> getTimeListForNewSale(){
		return new ResponseEntity<>(scheduleServices.viewAllTimeSlotsForBlockout("Tuesday"), HttpStatus.OK);
	}

	@GetMapping("clients")
	public ResponseEntity<List<ScheduleClientModel>> getClientList(){
		return new ResponseEntity<>(scheduleServices.viewClientListForSchedule(), HttpStatus.OK);
	}

	@GetMapping("treatments")
	public ResponseEntity<List<ScheduleTreatmentModel>> getAllTreatments() {
		return new ResponseEntity<>(scheduleServices.viewTreatmentListForSchedule(), HttpStatus.OK);
	}

	@PostMapping("book")
	public ResponseEntity<BookingCreatedModel> createBookingForEmployee(
			@RequestBody SaleNewBookingModel saleNewBookingModel
	){
		return new ResponseEntity<>(salesServices.createBookingForClientDuringCashUp(saleNewBookingModel), HttpStatus.CREATED);
	}

	@PostMapping("booking/capture")
	public ResponseEntity<SalesItemCaptured> captureSalesItemFromBooking(
	        @RequestBody SalesItemToCaptureModel salesItemToCaptureModel
	){
		return new ResponseEntity<>(salesServices.captureSalesItem(salesItemToCaptureModel), HttpStatus.CREATED);
	}

	@PutMapping("booking/complete")
	public ResponseEntity<SalesEditedBookingModel> createBookingForEmployee(
			@RequestBody SalesEditBookingModel salesEditBookingModel
	){
		return new ResponseEntity<>(salesServices.editBookingAndComplete(salesEditBookingModel), HttpStatus.OK);
	}

	@PutMapping("cash-up/complete")
	public ResponseEntity<SalesCashupCompletedModel> completeCashupForDay(
			@RequestHeader("Authorization") String authorization,
			@RequestBody SalesCashupCompleteModel salesCashupCompleteModel
	){
		return new ResponseEntity<>(salesServices.completeCashup(authorization, salesCashupCompleteModel), HttpStatus.OK);
	}

	@PostMapping("product/capture")
	public ResponseEntity<SalesItemCaptured> captureSalesItemFromBooking(
			@RequestBody ProductItemToCaptureModel productItemToCaptureModel
	){
		return new ResponseEntity<>(salesServices.captureSalesProductItem(productItemToCaptureModel), HttpStatus.OK);
	}

	@GetMapping("products")
	public ResponseEntity<List<ViewProductModel>> getAllProducts(){
		return new ResponseEntity<>(productServices.viewAllActiveProducts(), HttpStatus.OK);
	}
}
