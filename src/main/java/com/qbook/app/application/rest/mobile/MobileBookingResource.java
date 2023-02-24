package com.qbook.app.application.rest.mobile;

import com.qbook.app.application.models.BookingCancellationModel;
import com.qbook.app.application.models.BookingViewModel;
import com.qbook.app.application.services.appservices.AuthTokenServices;
import com.qbook.app.application.services.appservices.BookingServices;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
@RequestMapping("/api/mobile/bookings")
@AllArgsConstructor
public class MobileBookingResource {
	private final AuthTokenServices authTokenServices;
	private final BookingServices bookingServices;

	@GetMapping("{date}")
	public ResponseEntity<List<BookingViewModel>> viewClientBookingsForDate(@RequestHeader("Authorization") String Authorization, @PathVariable("date") String date) {
        log.info("MobileProfileResource.viewClientBookingsForDate() called at " + System.currentTimeMillis());
        log.info("MobileProfileResource.viewClientBookingsForDate() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(bookingServices.allBookingsForDateAndClient(date, authTokenServices.extractUserId(Authorization)), HttpStatus.OK);
	}

	@PutMapping("{bookingId}/cancel")
	public ResponseEntity<BookingCancellationModel> cancelBooking(@RequestHeader("Authorization") String Authorization, @PathVariable("bookingId") String bookingId) {
        log.info("MobileProfileResource.cancelBooking() called at " + System.currentTimeMillis());
        log.info("MobileProfileResource.cancelBooking() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(bookingServices.cancel(new ObjectId(bookingId), new ObjectId(authTokenServices.extractUserId(Authorization))), HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<BookingViewModel>> viewAllClientBookings(@RequestHeader("Authorization") String Authorization) {
        log.info("MobileProfileResource.viewAllClientBookings() called at " + System.currentTimeMillis());
        log.info("MobileProfileResource.viewAllClientBookings() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(bookingServices.allBookingsForClient(authTokenServices.extractUserId(Authorization)), HttpStatus.OK);
	}

	@GetMapping("filter")
	public ResponseEntity<List<BookingViewModel>> filterClientBookings(
			@RequestHeader("Authorization") String Authorization,
			@RequestParam("startDate") Long startDate,
			@RequestParam("endDate") Long endDate,
			@RequestParam("status") String status
	) {
		log.info("MobileProfileResource.filterClientBookings() called at " + System.currentTimeMillis());
		log.info("MobileProfileResource.filterClientBookings() ended at " + System.currentTimeMillis());
		return new ResponseEntity<>(bookingServices.filterBookingsForClient(
				authTokenServices.extractUserId(Authorization),
				startDate,
				endDate,
				status
		), HttpStatus.OK);
	}
}
