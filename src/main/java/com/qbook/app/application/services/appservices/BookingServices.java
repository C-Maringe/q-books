package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.BookingCancellationModel;
import com.qbook.app.application.models.BookingModel;
import com.qbook.app.application.models.BookingViewModel;
import com.qbook.app.application.models.scheduleModels.ScheduleBookingsModels;
import com.qbook.app.domain.models.Booking;
import org.bson.types.ObjectId;

import java.util.List;

public interface BookingServices {
	
    BookingCancellationModel cancelBooking(ObjectId bookingId, ObjectId userId);

	BookingCancellationModel cancel(ObjectId bookingId, ObjectId clientId);

	List<ScheduleBookingsModels> getBookingsBetweenStartAndEnd(Long startTime, Long endTime, String employeeId, String authToken);

	ScheduleBookingsModels viewSpecificBooking(String bookingId, String authorization);

	List<BookingModel> getAllBookingsForDate(String date);

    List<Booking> allBookingsForDate(String date);

	List<BookingViewModel> allBookingsForDateAndClient(String date, String clientId);

	List<BookingViewModel> allBookingsForClient(String clientId);

	List<BookingViewModel> filterBookingsForClient(String clientId, Long startDate, Long endDate, String status);
}
