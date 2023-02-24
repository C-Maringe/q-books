package com.qbook.app.application.rest.mobile;

import com.qbook.app.application.models.BookingCreatedModel;
import com.qbook.app.application.models.BookingItemModel;
import com.qbook.app.application.models.NewBookingModel;
import com.qbook.app.application.models.TimeSlotModel;
import com.qbook.app.application.models.employeeModels.EmployeeScheduleModel;
import com.qbook.app.application.services.appservices.ScheduleServices;
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
        return new ResponseEntity<>(scheduleServices.createBookingForClient(Authorization, newBookingModel), HttpStatus.CREATED);
	}
}
