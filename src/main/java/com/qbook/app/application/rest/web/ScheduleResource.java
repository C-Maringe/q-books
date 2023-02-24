package com.qbook.app.application.rest.web;

import com.qbook.app.application.models.*;
import com.qbook.app.application.models.employeeModels.EmployeeScheduleModel;
import com.qbook.app.application.models.scheduleModels.*;
import com.qbook.app.application.models.webPlatformModels.UpdateBookingModel;
import com.qbook.app.application.services.appservices.*;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Log
@RestController
@RequestMapping("/api/auth/schedule")
@AllArgsConstructor
public class ScheduleResource {
    private final BookingServices bookingService;
    private final AuthTokenServices authTokenServices;
    private final BookingCancellationQueueService bookingCancellationQueueService;
    private final ScheduleServices scheduleServices;
	private final BlockOutDayService blockOutDayService;
    private final UserServices userServices;

    @GetMapping("accepted-terms")
    public ResponseEntity<ScheduleUserAcceptedTermsModel> checkClientAcceptedBookingSystemTerms(
            @RequestHeader("Authorization") String authorization
    ) {
        return new ResponseEntity<>(userServices.checkIfUserHasAcceptedTerms(authorization), HttpStatus.OK);
    }

    @PutMapping("accept-terms")
    public ResponseEntity<ScheduleUserHasAcceptedTermsModel> clientAcceptTermsAndConditions(
            @RequestHeader("Authorization") String authorization
    ) {
        return new ResponseEntity<>(userServices.userHasAcceptedTerms(authorization), HttpStatus.OK);
    }

    // Booking process
    @GetMapping("employees")
    public ResponseEntity<List<EmployeeScheduleModel>> getAllActiveEmployees() {
        return new ResponseEntity<>(scheduleServices.getAllActiveEmployeesForSchedule(), HttpStatus.OK);
    }

    @GetMapping("bookings/{employeeId}")
    public ResponseEntity<List<ScheduleBookingsModels>> getAllBookingsForSchedule(
            @RequestHeader("Authorization") String Authorization,
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @PathVariable("employeeId") String employeeId){

        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(start);
            Calendar appointmentMaybeDate = Calendar.getInstance();
            appointmentMaybeDate.setTime(d);
            Long startDateTime = appointmentMaybeDate.getTimeInMillis();

            d = new SimpleDateFormat("yyyy-MM-dd").parse(end);
            appointmentMaybeDate = Calendar.getInstance();
            appointmentMaybeDate.setTime(d);
            Long endDateTime = appointmentMaybeDate.getTimeInMillis();
            return new ResponseEntity<>(bookingService.getBookingsBetweenStartAndEnd(startDateTime, endDateTime, employeeId, Authorization), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("booking/{bookingId}")
    public ResponseEntity<ScheduleBookingsModels> getAllBookingsForSchedule(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("bookingId") String bookingId){
        return new ResponseEntity<>(bookingService.viewSpecificBooking(bookingId, authorization), HttpStatus.OK);
    }

    @PutMapping("cancel/{bookingId}")
    public ResponseEntity<BookingCancellationModel> cancelBooking(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("bookingId") String bookingId
    ){
        return new ResponseEntity<>(bookingService.cancelBooking(new ObjectId(bookingId), new ObjectId(authTokenServices.extractUserId(authorization))), HttpStatus.OK);
    }

    @GetMapping("times/{employeeId}/{date}")
    public ResponseEntity<List<TimeSlotModel>> getTimeList(@PathVariable("employeeId") String employeeId, @PathVariable("date") String date){
        return new ResponseEntity<>(scheduleServices.viewAllTimeSlotsForEmployeeOnDateForWeb(employeeId,date), HttpStatus.OK);
    }

    @GetMapping("clients")
    public ResponseEntity<List<ScheduleClientModel>> getClientList(){
        return new ResponseEntity<>(scheduleServices.viewClientListForSchedule(), HttpStatus.OK);
    }

    @GetMapping("treatments/{employeeType}")
    public ResponseEntity<List<ScheduleTreatmentModel>> getAllTreatments(@PathVariable("employeeType") String employeeType) {
        return new ResponseEntity<>(scheduleServices.viewTreatmentListForScheduleAndEmployeeType(employeeType), HttpStatus.OK);
    }

	@GetMapping("treatments/{employeeType}/{startDate}")
	public ResponseEntity<List<ScheduleTreatmentModel>> getAllTreatmentsForEmployeeTypeAndBookingDate(
			@PathVariable("employeeType") String employeeType,
			@PathVariable("startDate") String startDate
	) {
		return new ResponseEntity<>(scheduleServices.viewTreatmentListForScheduleAndEmployeeTypeAndDate(employeeType, startDate), HttpStatus.OK);
	}

	@PostMapping("employee-book")
	public ResponseEntity<BookingCreatedModel> createBookingForEmployee(
			@RequestBody ScheduleNewBookingModel scheduleNewBookingModel
	){
		return new ResponseEntity<>(scheduleServices.createBookingForClientByEmployee(scheduleNewBookingModel), HttpStatus.CREATED);
	}

	@PostMapping("client-book")
	public ResponseEntity<BookingCreatedModel> createBookingForClient(
			@RequestHeader("Authorization") String authorization,
			@RequestBody NewBookingModel newBookingModel
	){
		return new ResponseEntity<>(scheduleServices.createBookingForClient(authorization, newBookingModel), HttpStatus.CREATED);
	}

    @GetMapping("blockout-time/times/{date}")
    public ResponseEntity<List<TimeSlotModel>> viewAllTimeSlotsForBlockout(@PathVariable("date") String date){
        return new ResponseEntity<>(scheduleServices.viewAllTimeSlotsForBlockout(date), HttpStatus.OK);
    }

	@PostMapping("blockout-time")
	public ResponseEntity<ScheduleBlockoutTimeCreatedModel> createBlockoutTime(@RequestBody ScheduleNewBlockoutTimeModel scheduleNewBlockoutTimeModel){
		return new ResponseEntity<>(blockOutDayService.blockoutScheduleTime(scheduleNewBlockoutTimeModel), HttpStatus.CREATED);
	}

//     CANCELLATION QUEUE
    @PostMapping("booking-cancellation")
    public ResponseEntity<BookingCancellationQueueModel> addClientToBookingCancellationQueue(@RequestHeader("Authorization") String Authorization, @RequestBody BookingCancellationQueueMember bookingCancellationQueueMember) {
        bookingCancellationQueueMember.setClientId(authTokenServices.extractUserId(Authorization));
        return new ResponseEntity<>(bookingCancellationQueueService.addClientToQueue(bookingCancellationQueueMember), HttpStatus.CREATED);
    }

    @GetMapping("booking-cancellation")
    public ResponseEntity<List<BookingCancellationMember>> getBookingCancellationQueue() {
        return new ResponseEntity<>(bookingCancellationQueueService.viewAllCancellationQueueClients(), HttpStatus.OK);
    }

    @GetMapping("booking-cancellation/times/{date}")
    public ResponseEntity<List<TimeSlotModel>> getTimeListForCancellationQueue(@PathVariable("date") String date){
        return new ResponseEntity<>(scheduleServices.viewAllTimeSlots(date), HttpStatus.OK);
    }
}
