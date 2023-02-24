package com.qbook.app.application.services.specifications;

import com.qbook.app.application.models.NewBookingModel;
import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.BookingCreatedBy;

public interface BookingValidationService {
	Booking validateBookingAndCreate(String clientId, NewBookingModel newBookingModel, BookingCreatedBy bookingCreatedBy);

	Booking skipValidateAndCreate(String clientId, NewBookingModel newBookingModel, BookingCreatedBy bookingCreatedBy);
}
