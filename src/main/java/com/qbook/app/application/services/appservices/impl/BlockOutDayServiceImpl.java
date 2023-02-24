package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.blockOutExceptions.BlockoutDayFailedException;
import com.qbook.app.application.models.scheduleModels.ScheduleBlockoutTimeCreatedModel;
import com.qbook.app.application.models.scheduleModels.ScheduleNewBlockoutTimeForWorkingDayModel;
import com.qbook.app.application.models.scheduleModels.ScheduleNewBlockoutTimeModel;
import com.qbook.app.application.services.appservices.BlockOutDayService;
import com.qbook.app.application.services.specifications.BlockBookingsSpecifications;
import com.qbook.app.application.services.specifications.impl.BlockBookingsSpecificationsImpl;
import com.qbook.app.domain.models.Booking;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Log
@AllArgsConstructor
@Service
public class BlockOutDayServiceImpl implements BlockOutDayService {

	private final BlockBookingsSpecifications blockBookingSpecifications;
    @Override
    public ScheduleBlockoutTimeCreatedModel blockoutScheduleTime(ScheduleNewBlockoutTimeModel scheduleNewBlockoutTimeModel) {
	    List<Booking> bookings = blockBookingSpecifications.validateBookingsAndCreate(scheduleNewBlockoutTimeModel);

	    if (bookings.size() == scheduleNewBlockoutTimeModel.getEmployees().length) {
		    return new ScheduleBlockoutTimeCreatedModel("The day and time have successfully been blocked out for " + bookings.size() + ((bookings.size() > 1) ? " employees" : " employee"));
	    } else {
	        throw new BlockoutDayFailedException("We failed to block out the allocated time for one of the employees. Please ensure there are bookings already.");
	    }
    }

	@Override
	public ScheduleBlockoutTimeCreatedModel blockoutScheduleTimeForEmployeeHours(ScheduleNewBlockoutTimeForWorkingDayModel scheduleNewBlockoutTimeForWorkingDayModel) {
		List<Booking> bookings = blockBookingSpecifications.validateBookingsAndCreate(scheduleNewBlockoutTimeForWorkingDayModel);

		if (bookings.size() > 0) {
			return new ScheduleBlockoutTimeCreatedModel("The day and time have successfully been blocked out for " + bookings.size() + ((bookings.size() > 1) ? " employees" : " employee"));
		} else {
			throw new BlockoutDayFailedException("We failed to block out the allocated time for one of the employees. Please ensure there are bookings already.");
		}
	}
}
