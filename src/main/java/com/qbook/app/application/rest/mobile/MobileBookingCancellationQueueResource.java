package com.qbook.app.application.rest.mobile;

import com.qbook.app.application.models.BookingCancellationQueueMember;
import com.qbook.app.application.models.BookingCancellationQueueModel;
import com.qbook.app.application.models.NewJoinBookingCancellationQueueModel;
import com.qbook.app.application.services.appservices.AuthTokenServices;
import com.qbook.app.application.services.appservices.BookingCancellationQueueService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/api/mobile/booking-cancellation")
@AllArgsConstructor
public class MobileBookingCancellationQueueResource {
    private final AuthTokenServices authTokenServices;
    private final BookingCancellationQueueService bookingCancellationQueueService;

    @PostMapping
    public ResponseEntity<BookingCancellationQueueModel> addClientToBookingCancellationQueue
            (@RequestHeader("Authorization") String Authorization,@RequestBody NewJoinBookingCancellationQueueModel newJoinBookingCancellationQueueModel) {
        log.info( "MobileBookingCancellationQueueResource.addClientToBookingCancellationQueue() called at " + System.currentTimeMillis());
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yy-MM-dd");
        BookingCancellationQueueMember bookingCancellationQueueMember = new BookingCancellationQueueMember();
        bookingCancellationQueueMember.setStartTime(newJoinBookingCancellationQueueModel.getStartTime());
        bookingCancellationQueueMember.setEndTime(newJoinBookingCancellationQueueModel.getEndTime());
        bookingCancellationQueueMember.setStartDate(dateTimeFormatter.parseDateTime(newJoinBookingCancellationQueueModel.getStartDate()).getMillis());
        bookingCancellationQueueMember.setEndDate(dateTimeFormatter.parseDateTime(newJoinBookingCancellationQueueModel.getEndDate()).getMillis());
        bookingCancellationQueueMember.setClientId(authTokenServices.extractUserId(Authorization));
        bookingCancellationQueueMember.setEmployeeId(newJoinBookingCancellationQueueModel.getEmployeeId());
        log.info( "MobileBookingCancellationQueueResource.addClientToBookingCancellationQueue() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(bookingCancellationQueueService.addClientToQueue(bookingCancellationQueueMember), HttpStatus.CREATED);
    }
}
