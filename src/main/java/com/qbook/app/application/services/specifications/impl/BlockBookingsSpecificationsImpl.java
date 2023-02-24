package com.qbook.app.application.services.specifications.impl;

import com.qbook.app.application.configuration.exception.InvalidBookingException;
import com.qbook.app.application.configuration.exception.ResourceNotFoundException;
import com.qbook.app.application.configuration.exception.blockOutExceptions.BlockoutDayFailedException;
import com.qbook.app.application.configuration.exception.blockOutExceptions.StartDateToOldException;
import com.qbook.app.application.models.configurationModels.ApplicationConfigurationModel;
import com.qbook.app.application.models.scheduleModels.ScheduleNewBlockoutTimeForWorkingDayModel;
import com.qbook.app.application.models.scheduleModels.ScheduleNewBlockoutTimeModel;
import com.qbook.app.application.services.appservices.ApplicationConfigurationsServices;
import com.qbook.app.application.services.specifications.BlockBookingsSpecifications;
import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.Employee;
import com.qbook.app.domain.models.WorkingDay;
import com.qbook.app.domain.repository.BookingRepository;
import com.qbook.app.domain.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log
@Service
@AllArgsConstructor
public class BlockBookingsSpecificationsImpl implements BlockBookingsSpecifications {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    private final BookingRepository bookingRepository;
	private final EmployeeRepository employeeRepository;
	private final ApplicationConfigurationsServices applicationConfigurationsServices;

	@Override
	public List<Booking> validateBookingsAndCreate(ScheduleNewBlockoutTimeModel scheduleNewBlockoutTimeModel) {
		List<Booking> bookingList = new ArrayList<>();
		DateTime starting = new DateTime(dateTimeFormatter.parseDateTime(scheduleNewBlockoutTimeModel.getStartDateTime()));
		DateTime ending = new DateTime(dateTimeFormatter.parseDateTime(scheduleNewBlockoutTimeModel.getEndDateTime()));
		ApplicationConfigurationModel applicationConfigurationModel = applicationConfigurationsServices.viewApplicationConfiguration();

		int counter = 0;
		for(int i = 0; i < scheduleNewBlockoutTimeModel.getEmployees().length; i++) {
			Optional<Employee> employeeOptional = employeeRepository.findById(new ObjectId(scheduleNewBlockoutTimeModel.getEmployees()[i]));

			if(!employeeOptional.isPresent()) {
				throw new ResourceNotFoundException("We were unable to find the employee");
			}

			bookingIsNotToOld(starting);

			noBookingClashes(starting, ending, employeeOptional.get());

			noBookingOverlaps(employeeOptional.get(), starting, ending);

			bookingDoesntEndAfterWorkHours(starting, ending, applicationConfigurationModel);

			noBookingStartOverlaps(employeeOptional.get(), starting);

			noBookingEndOverlaps(employeeOptional.get(), ending);

			noBookingInBetween(employeeOptional.get(), starting, ending);

			bookingStartsAfterTimeBetweenBookings(starting);

			bookingStartsBeforeWork(starting, applicationConfigurationModel);

			bookingIsOnCompanyAvailableDay(starting, applicationConfigurationModel);

			counter++;
		}

		if(counter == scheduleNewBlockoutTimeModel.getEmployees().length) {
			for(int i = 0; i < scheduleNewBlockoutTimeModel.getEmployees().length; i++) {
				Optional<Employee> employeeOptional = employeeRepository.findById(new ObjectId(scheduleNewBlockoutTimeModel.getEmployees()[i]));

				if(!employeeOptional.isPresent()) {
					throw new ResourceNotFoundException("We were unable to find the employee");
				}

				Booking blockoutDay = new Booking();
				blockoutDay.setBlockedDayTitle(scheduleNewBlockoutTimeModel.getBlockoutTimeTitle());
				blockoutDay.setDayToBlockOut(true);
				blockoutDay.setStartDateTime(starting.getMillis());
				blockoutDay.setEndDateTime(ending.getMillis());
				blockoutDay.setDuration(Minutes.minutesBetween(starting, ending).getMinutes());
				blockoutDay.setEmployee(employeeOptional.get());

				bookingList.add(blockoutDay);
				bookingRepository.save(blockoutDay);
			}

			return bookingList;
		} else {
			throw new BlockoutDayFailedException("We failed to block out the allocated time for one of the employees. Please ensure there are bookings already.");
		}
	}

	@Override
	public List<Booking> validateBookingsAndCreate(ScheduleNewBlockoutTimeForWorkingDayModel scheduleNewBlockoutTimeForWorkingDayModel) {
		List<Booking> bookingList = new ArrayList<>();
		DateTime starting = new DateTime(dateTimeFormatter.parseDateTime(scheduleNewBlockoutTimeForWorkingDayModel.getStartDateTime()));
		DateTime ending = new DateTime(dateTimeFormatter.parseDateTime(scheduleNewBlockoutTimeForWorkingDayModel.getEndDateTime()));
		ApplicationConfigurationModel applicationConfigurationModel = applicationConfigurationsServices.viewApplicationConfiguration();

		Optional<Employee> employeeOptional = employeeRepository.findById(new ObjectId(scheduleNewBlockoutTimeForWorkingDayModel.getEmployeesId()));

		if(!employeeOptional.isPresent()) {
			throw new ResourceNotFoundException("We were unable to find the employee");
		}

		bookingIsNotToOld(starting);

		noBookingClashes(starting, ending, employeeOptional.get());

		noBookingOverlaps(employeeOptional.get(), starting, ending);

		bookingDoesntEndAfterWorkHours(starting, ending, applicationConfigurationModel);

		noBookingStartOverlaps(employeeOptional.get(), starting);

		noBookingEndOverlaps(employeeOptional.get(), ending);

		noBookingInBetween(employeeOptional.get(), starting, ending);

		bookingStartsAfterTimeBetweenBookings(starting);

		bookingStartsBeforeWork(starting, applicationConfigurationModel);

		bookingIsOnCompanyAvailableDay(starting, applicationConfigurationModel);

		Booking blockoutDay = new Booking();
		blockoutDay.setBlockedDayTitle(scheduleNewBlockoutTimeForWorkingDayModel.getBlockoutTimeTitle());
		blockoutDay.setDayToBlockOut(true);
		blockoutDay.setStartDateTime(starting.getMillis());
		blockoutDay.setEndDateTime(ending.getMillis());
		blockoutDay.setDuration(Minutes.minutesBetween(starting, ending).getMinutes());
		blockoutDay.setEmployee(employeeOptional.get());
		blockoutDay.setWorkingDay(true);
		blockoutDay.setWorkingDayId(scheduleNewBlockoutTimeForWorkingDayModel.getWorkingDayId());
		bookingList.add(blockoutDay);
		bookingRepository.save(blockoutDay);

		return bookingList;
	}

	private void bookingIsNotToOld(DateTime startDateTime) {
		DateTime start = new DateTime(startDateTime);

		if(start.isBeforeNow()){
			throw new InvalidBookingException("Please ensure the booking is made a for a future date and time.");
		}
	}

	private void bookingDoesntEndAfterWorkHours(DateTime startDateTime, DateTime endDateTime, ApplicationConfigurationModel applicationConfigurationModel) {
		String dayOfWeek = startDateTime.toString(DateTimeFormat.forPattern("EEEE"));
		WorkingDay workingDay = getWorkingDayConfiguration(dayOfWeek, applicationConfigurationModel);

		//end time
		LocalTime companyEndTime = LocalTime.parse(workingDay.getWorkEndTime(), DateTimeFormat.forPattern("HH:mm"));

		// the booking must not start or end after work hours
		if(endDateTime.toLocalTime().isAfter(companyEndTime)){
			throw new InvalidBookingException("Please ensure the booking does not end after " + companyEndTime.toString(DateTimeFormat.forPattern("HH:mm")));
		}

		if(startDateTime.toLocalTime().isAfter(companyEndTime)){
			throw new InvalidBookingException("Please ensure the booking does not start after " + companyEndTime.toString(DateTimeFormat.forPattern("HH:mm")));
		}
	}

	private void noBookingClashes(DateTime startDateTime, DateTime endDateTime, Employee toCheckEmployee) {
		List<Booking> whereStartTimeOverlapsAnotherBooking = bookingRepository.findAllByEndDateTimeGreaterThanAndStartDateTimeLessThanAndBookingStatus(
				startDateTime.getMillis(), startDateTime.getMillis(), "Active"
		);
		List<Booking> whereEndTimeOverlapsAnotherBooking = bookingRepository.findAllByEndDateTimeGreaterThanAndStartDateTimeLessThanAndBookingStatus(
				endDateTime.getMillis(), endDateTime.getMillis(), "Active"
		);

		for(Booking b: whereStartTimeOverlapsAnotherBooking){
			if(b.getEmployee().getId().equals(toCheckEmployee.getId())){
				throw new InvalidBookingException("Your booking is clashing with a booking starting at " + new LocalTime(b.getStartDateTime()).toString(DateTimeFormat.forPattern("HH:mm")));
			}
		}

		for(Booking b: whereEndTimeOverlapsAnotherBooking){
			if(b.getEmployee().getId().equals(toCheckEmployee.getId())){
				throw new InvalidBookingException("Your booking is clashing with a booking starting at " + new LocalTime(b.getStartDateTime()).toString(DateTimeFormat.forPattern("HH:mm")));
			}
		}
	}

	private void noBookingOverlaps(Employee toCheckEmployee, DateTime startDateTime, DateTime endDateTime) {
		// start date time must be less than endDateTime and greater than the start date time
		//booking: 10:15 - 10:55 must clash with a booking at 10:00 - 11:00
		Optional<Booking> bookingOverlapping = bookingRepository.findAllByEndDateTimeGreaterThanAndStartDateTimeLessThanAndBookingStatus(startDateTime.getMillis(), endDateTime.getMillis(), "Active")
				.stream()
				.filter(booking -> booking.getEmployee().getId().equals(toCheckEmployee.getId()))
				.findFirst();

		if(bookingOverlapping.isPresent()) {
			throw new InvalidBookingException("Your booking is overlapping with a booking starting at " + new LocalTime(bookingOverlapping.get().getStartDateTime()).toString(DateTimeFormat.forPattern("HH:mm")));
		}
	}

	private void noBookingStartOverlaps(Employee toCheckEmployee, DateTime startDateTime) {
		// start date time must be less than endDateTime and greater than the start date time
		//booking: 10:40 - 11:45 must clash with a booking at 10:00 - 11:00
		Optional<Booking> bookingOverlapping = bookingRepository.findAllByEndDateTimeGreaterThanAndStartDateTimeLessThanAndBookingStatus(startDateTime.getMillis(), startDateTime.getMillis(), "Active")
				.stream()
				.filter(booking -> booking.getEmployee().getId().equals(toCheckEmployee.getId()))
				.findFirst();

		if(bookingOverlapping.isPresent()) {
			throw new InvalidBookingException("Your booking is overlapping with a booking starting at " + new LocalTime(bookingOverlapping.get().getStartDateTime()).toString(DateTimeFormat.forPattern("HH:mm")));
		}
	}

	private void noBookingEndOverlaps(Employee toCheckEmployee, DateTime endDateTime) {
		// end date time must be less than endDateTime and greater than the start date time
		//booking: 09:55 - 10:45 must clash with a booking at 10:00 - 11:00
		Optional<Booking> bookingOverlapping = bookingRepository.findAllByEndDateTimeGreaterThanAndStartDateTimeLessThanAndBookingStatus(endDateTime.getMillis(), endDateTime.getMillis(), "Active")
				.stream()
				.filter(booking -> booking.getEmployee().getId().equals(toCheckEmployee.getId()))
				.findFirst();

		if(bookingOverlapping.isPresent()) {
			throw new InvalidBookingException("Your booking is overlapping with a booking starting at " + new LocalTime(bookingOverlapping.get().getStartDateTime()).toString(DateTimeFormat.forPattern("HH:mm")));
		}
	}

	private void noBookingInBetween(Employee toCheckEmployee, DateTime startDateTime, DateTime endDateTime) {
		// startDateTime must be greater than booking start time and end date time must be less than endDateTime
		//	booking: 09:55 - 11:25 must clash with a booking at 10:00 - 11:00
		Optional<Booking> bookingOverlapping = bookingRepository.findAllByEndDateTimeGreaterThanAndStartDateTimeLessThanAndBookingStatus(endDateTime.getMillis(), startDateTime.getMillis(), "Active")
				.stream()
				.filter(booking -> booking.getEmployee().getId().equals(toCheckEmployee.getId()))
				.findFirst();

		if(bookingOverlapping.isPresent()) {
			throw new InvalidBookingException("Your booking is overlapping with a booking starting at " + new LocalTime(bookingOverlapping.get().getStartDateTime()).toString(DateTimeFormat.forPattern("HH:mm")));
		}
	}

	private void bookingStartsAfterTimeBetweenBookings(DateTime startDateTime) {
		bookingRepository.findAllByEndDateTime(new LocalDateTime(startDateTime).minusMinutes(1).toDateTime().getMillis())
				.forEach(booking -> {
					throw new InvalidBookingException("Your booking is overlapping with a booking starting at " + startDateTime.toString(DateTimeFormat.forPattern("HH:mm")));
				});
	}

	private void bookingStartsBeforeWork(DateTime startDateTime, ApplicationConfigurationModel applicationConfigurationModel) {
		// check which day it is booked on to know the start time
		String dayOfWeek = startDateTime.toString(DateTimeFormat.forPattern("EEEE"));
		WorkingDay workingDay = getWorkingDayConfiguration(dayOfWeek, applicationConfigurationModel);

		LocalTime companyStartTime = LocalTime.parse(workingDay.getWorkStartTime(), DateTimeFormat.forPattern("HH:mm"));

		if(startDateTime.toLocalTime().isBefore(companyStartTime)) {
			throw new InvalidBookingException("Please ensure the booking does not start before the company opens.");
		}
	}

	private void bookingIsOnCompanyAvailableDay(DateTime startDateTime, ApplicationConfigurationModel applicationConfigurationModel) {

		String dayOfWeek = startDateTime.toString(DateTimeFormat.forPattern("EEEE"));

		getWorkingDayConfiguration(dayOfWeek, applicationConfigurationModel);
	}

	private WorkingDay getWorkingDayConfiguration(String dayOfWeek, ApplicationConfigurationModel applicationConfigurationModel) {
		Optional<WorkingDay> workingDayOptional = applicationConfigurationModel
				.getWorkingDays()
				.stream()
				.filter(workingDay -> workingDay.getWorkingDay().equalsIgnoreCase(dayOfWeek))
				.findFirst();

		if(!workingDayOptional.isPresent()) {
			throw new InvalidBookingException("Your booking time is on a day the company is closed, please ensure you select an open day.");
		} else {
			return workingDayOptional.get();
		}
	}
}
