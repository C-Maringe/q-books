package com.qbook.app.application.rest.mobile;

import com.qbook.app.application.models.BookingCreatedModel;
import com.qbook.app.application.models.BookingItemModel;
import com.qbook.app.application.models.NewBookingModel;
import com.qbook.app.application.models.TimeSlotModel;
import com.qbook.app.application.models.employeeModels.EmployeeScheduleModel;
import com.qbook.app.application.models.employeeModels.ViewEmployeeType;
import com.qbook.app.application.models.productModels.ViewProductModel;
import com.qbook.app.application.models.scheduleModels.ScheduleTreatmentModel;
import com.qbook.app.application.models.treatmentModels.ViewTreatmentModel;
import com.qbook.app.application.services.appservices.ProductServices;
import com.qbook.app.application.services.appservices.ScheduleServices;
import com.qbook.app.application.services.appservices.SuperUserServices;
import com.qbook.app.application.services.appservices.TreatmentServices;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
@RequestMapping("/api/mobile/schedule")
@AllArgsConstructor
public class MobileScheduleResource {
	private final ScheduleServices scheduleServices;
	private final ProductServices productServices;
	private final TreatmentServices treatmentServices;
	private final SuperUserServices superUserServices;

	@GetMapping("employees")
	public ResponseEntity<List<EmployeeScheduleModel>> viewClientBookingsForDate(@RequestHeader("Authorization") String Authorization) {
        log.info("MobileScheduleResource.viewClientBookingsForDate() called at " + System.currentTimeMillis());
        log.info("MobileScheduleResource.viewClientBookingsForDate() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(scheduleServices.getAllActiveEmployeesForSchedule(), HttpStatus.OK);
	}

	@GetMapping("employees/{employeeId}/{date}")
	public ResponseEntity<List<TimeSlotModel>> viewEmployeesTimeAvailableForDate(@RequestHeader("Authorization") String Authorization, @PathVariable("employeeId") String employeeId, @PathVariable("date") String date) {
        log.info("MobileScheduleResource.viewEmployeesTimeAvailableForDate() called at " + System.currentTimeMillis());
        log.info("MobileScheduleResource.viewEmployeesTimeAvailableForDate() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(scheduleServices.viewAllTimeSlotsForEmployeeOnDate(employeeId,date), HttpStatus.OK);
	}

	@GetMapping("items/{employeeId}")
	public ResponseEntity<List<BookingItemModel>> viewBookingItemsForEmployeeId(@RequestHeader("Authorization") String Authorization, @PathVariable("employeeId") String employeeId) {
        log.info("MobileScheduleResource.viewBookingItemsForEmployeeId() called at " + System.currentTimeMillis());
        log.info("MobileScheduleResource.viewBookingItemsForEmployeeId() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(scheduleServices.viewAllBookingItemsPerEmployee(employeeId), HttpStatus.OK);
	}

	@PostMapping("book")
	public ResponseEntity<BookingCreatedModel> bookClient(@RequestHeader("Authorization") String Authorization, @RequestBody NewBookingModel newBookingModel) {
        log.info("MobileBookingCancellationQueueResource.bookClient() called at " + System.currentTimeMillis());
        log.info("MobileBookingCancellationQueueResource.bookClient() ended at " + System.currentTimeMillis());
		if(Authorization.startsWith("Bearer")){
			Authorization = Authorization.substring(7);
		}
		log.info(Authorization);
        return new ResponseEntity<>(scheduleServices.createBookingForClient(Authorization, newBookingModel), HttpStatus.CREATED);
	}

	@GetMapping("treatments")
	public ResponseEntity<List<ScheduleTreatmentModel>> getAllTreatments() {
		return new ResponseEntity<>(scheduleServices.viewTreatmentListForSchedule(), HttpStatus.OK);
	}

	@GetMapping("all-treatments")
	public ResponseEntity<List<ViewTreatmentModel>> getAllTreatmentsNow(){
		return new ResponseEntity<>(treatmentServices.viewAllTreatments(), HttpStatus.OK);
	}

	@GetMapping("products")
	public ResponseEntity<List<ViewProductModel>> getAllProducts(){
		return new ResponseEntity<>(productServices.viewAllProducts(), HttpStatus.OK);
	}

	@GetMapping("employee-types")
	public ResponseEntity<List<ViewEmployeeType>> viewEmployeeTypes(){
		return new ResponseEntity<>(superUserServices.getAllEmployeeTypes(), HttpStatus.OK);
	}
}
