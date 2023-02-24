package com.qbook.app.application.rest.web;

import com.qbook.app.application.models.BookingCancellationModel;
import com.qbook.app.application.models.BookingModel;
import com.qbook.app.application.models.NotificationSuccessModel;
import com.qbook.app.application.models.NotifySpecificClientModel;
import com.qbook.app.application.services.appservices.AuthTokenServices;
import com.qbook.app.application.services.appservices.BookingServices;
import com.qbook.app.application.services.appservices.NotificationsService;
import com.qbook.app.utilities.CustomHTTPHeaders;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;

@Log
@RestController
@RequestMapping("/api/auth/bookings")
@AllArgsConstructor
public class BookingResource {
	private final BookingServices bookingServices;
	private final AuthTokenServices authTokenServices;
	private final NotificationsService notificationsService;

	@GetMapping("/{date}")
    public ResponseEntity<List<BookingModel>> getAllBookingsForDate(@PathVariable("date") String date) {
        log.log(Level.INFO, "BookingResource.getAllBookingsForDate() called at " + System.currentTimeMillis());
        return new ResponseEntity<>(bookingServices.getAllBookingsForDate(date), HttpStatus.OK);
    }

	@PutMapping("{bookingId}/cancel")
	public ResponseEntity<BookingCancellationModel> cancelBooking
			(@RequestHeader("Authorization") String Authorization, @PathVariable("bookingId") String bookingId) {

		if(Authorization.startsWith("Bearer")){
			Authorization = Authorization.substring(7);
		}
		else{
			Authorization = Authorization;
		}
		log.log(Level.INFO, "BookingResource.cancelBooking() called at " + System.currentTimeMillis());
		log.log(Level.INFO, "BookingResource.cancelBooking() ended at " + System.currentTimeMillis());
		return new ResponseEntity<>(bookingServices.cancelBooking(new ObjectId(bookingId), new ObjectId(authTokenServices.extractUserId(Authorization))), HttpStatus.OK);
	}

	@PutMapping("notify/{date}")
	public ResponseEntity<NotificationSuccessModel> notifyAllClientsOnDay(@PathVariable("date") String date) {
		log.log(Level.INFO, "BookingResource.notifyAllClientsOnDay() called at " + System.currentTimeMillis());
		return new ResponseEntity<>(notificationsService.sendReminderNotificationForAllClientsForSpecificDay(date), HttpStatus.OK);
	}

	@PutMapping("notify")
	public ResponseEntity<NotificationSuccessModel> notifySpecificClientOnDay(@RequestBody NotifySpecificClientModel notifySpecificClientModel) {
		log.log(Level.INFO, "BookingResource.notifyAllClientsOnDay() called at " + System.currentTimeMillis());
		return new ResponseEntity<>(notificationsService.sendReminderNotificationForToSpecificClientsForSpecificDay(notifySpecificClientModel), HttpStatus.OK);
	}
}
