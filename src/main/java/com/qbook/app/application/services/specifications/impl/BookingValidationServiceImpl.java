package com.qbook.app.application.services.specifications.impl;

import com.qbook.app.application.configuration.exception.InvalidBookingException;
import com.qbook.app.application.configuration.exception.InvalidClientException;
import com.qbook.app.application.configuration.exception.InvalidEmployeeException;
import com.qbook.app.application.configuration.exception.bookingsExceptions.TreatmentSpecialEndDateExceededException;
import com.qbook.app.application.models.NewBookingModel;
import com.qbook.app.application.models.configurationModels.ApplicationConfigurationModel;
import com.qbook.app.application.services.appservices.ApplicationConfigurationsServices;
import com.qbook.app.application.services.specifications.BookingValidationService;
import com.qbook.app.domain.models.*;
import com.qbook.app.domain.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Log
@Service
@AllArgsConstructor
public class BookingValidationServiceImpl implements BookingValidationService {
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
	private final BookingRepository bookingRepository;
	private final EmployeeRepository employeeRepository;
	private final ClientRepository clientRepository;
	private final ApplicationConfigurationsServices applicationConfigurationsServices;
	private final TreatmentRepository treatmentRepository;
	private final BookingListItemRepository bookingListItemRepository;
	private final BookingListRepository bookingListRepository;

	@Override
	public Booking validateBookingAndCreate(String clientId, NewBookingModel newBookingModel, BookingCreatedBy bookingCreatedBy) {
		DateTime proposedStartDateTime = dateTimeFormatter.parseDateTime(newBookingModel.getStartDateTime());

		Booking booking = new Booking();
		booking.setId(new ObjectId());

		Optional<Employee> employee = employeeRepository.findById(new ObjectId(newBookingModel.getEmployeeId()));

		if(!employee.isPresent()) {
			throw new InvalidEmployeeException("The employee details could not be found, please contact the administrator.");
		}

		Optional<Client> client = clientRepository.findById(new ObjectId(clientId));

		if(!client.isPresent()) {
			throw new InvalidClientException("Your details could not be found, please contact the administrator.");
		}

		bookingIsWithinThreeMonthRange(proposedStartDateTime);

		bookingIsNotToOld(proposedStartDateTime);

		ApplicationConfigurationModel applicationConfigurationModel = applicationConfigurationsServices.viewApplicationConfiguration();

		bookingIsGivenProperNotice(proposedStartDateTime, applicationConfigurationModel);

		bookingStartsAfterTimeBetweenBookings(proposedStartDateTime);

		bookingStartsBeforeWork(proposedStartDateTime, applicationConfigurationModel);

		bookingIsOnCompanyAvailableDay(proposedStartDateTime, applicationConfigurationModel);

		Optional<Integer> bookingDuration =
				stream(newBookingModel.getNewBookingItemModel())
				.map(newBookingItemModel -> {
					Optional<Treatment> treatmentOptional = treatmentRepository.findById(new ObjectId(newBookingItemModel.getId()));

					return treatmentOptional.map(treatment -> treatment.getDuration() * newBookingItemModel.getQuantity()).orElse(0);
				})
				.reduce(Integer::sum);

		if(bookingDuration.isPresent()) {
			DateTime proposedEndDateTime = proposedStartDateTime.plusMinutes(bookingDuration.get());
			bookingDoesntEndAfterWorkHours(proposedStartDateTime, proposedEndDateTime, applicationConfigurationModel);

			noBookingClashes(proposedStartDateTime, proposedEndDateTime, employee.get());

			noBookingOverlaps(employee.get(), proposedStartDateTime, proposedEndDateTime);

			noBookingStartOverlaps(employee.get(), proposedStartDateTime);

			noBookingEndOverlaps(employee.get(), proposedEndDateTime);

			noBookingInBetween(employee.get(), proposedStartDateTime, proposedEndDateTime);

			booking.setClient(client.get());
			booking.setEmployee(employee.get());

			BookingList bookingList = new BookingList();
			bookingList.setId(new ObjectId());

			List<BookingListItem> bookingListItems = Arrays
					.stream(newBookingModel.getNewBookingItemModel())
					.map(newBookingItemModel -> {
						BookingListItem bookingListItem = new BookingListItem();
						bookingListItem.setId(new ObjectId());

						Optional<Treatment> treatmentOptional = treatmentRepository.findById(new ObjectId(newBookingItemModel.getId()));
						treatmentOptional.ifPresent(treatment -> {
							bookingListItem.setTreatmentQuantity(newBookingItemModel.getQuantity());
							bookingListItem.setTreatment(treatment);

							bookingListItemRepository.save(bookingListItem);
						});

						return bookingListItem;
					})
					.collect(Collectors.toList());

			bookingList.setBookingListItems(bookingListItems);

			bookingListRepository.save(bookingList);

			booking.setCreatedBy(bookingCreatedBy);
			booking.setDateCreated(DateTime.now().getMillis());
			booking.setBookingList(bookingList);
			booking.setDuration(bookingDuration.get());
			booking.setStartDateTime(proposedStartDateTime.getMillis());
			booking.setEndDateTime(proposedEndDateTime.getMillis());

			if(newBookingModel.isDepositRequired()) {
				booking.setBookingStatus("Pending Payment");
				booking.setDepositPaid(false);
			}
			bookingRepository.save(booking);
		}

		return booking;
	}

	@Override
	public Booking skipValidateAndCreate(String clientId, NewBookingModel newBookingModel, BookingCreatedBy bookingCreatedBy) {
		DateTime proposedStartDateTime = dateTimeFormatter.parseDateTime(newBookingModel.getStartDateTime());

		Booking booking = new Booking();
		booking.setId(new ObjectId());

		Optional<Employee> employee = employeeRepository.findById(new ObjectId(newBookingModel.getEmployeeId()));

		if(!employee.isPresent()) {
			throw new InvalidEmployeeException("The employee details could not be found, please contact the administrator.");
		}

		Optional<Client> client = clientRepository.findById(new ObjectId(clientId));

		if(!client.isPresent()) {
			throw new InvalidClientException("Your details could not be found, please contact the administrator.");
		}

		Optional<Integer> bookingDuration =
				stream(newBookingModel.getNewBookingItemModel())
				.map(newBookingItemModel -> {
					Optional<Treatment> treatmentOptional = treatmentRepository.findById(new ObjectId(newBookingItemModel.getId()));
					return treatmentOptional.map(treatment -> treatment.getDuration() * newBookingItemModel.getQuantity()).orElse(0);
				})
				.reduce(Integer::sum);


		if(bookingDuration.isPresent()) {
			DateTime proposedEndDateTime = proposedStartDateTime.plusMinutes(bookingDuration.get());

			booking.setClient(client.get());
			booking.setEmployee(employee.get());

			BookingList bookingList = new BookingList();
			bookingList.setId(new ObjectId());

			List<BookingListItem> bookingListItems = Arrays
					.stream(newBookingModel.getNewBookingItemModel())
					.map(newBookingItemModel -> {
						BookingListItem bookingListItem = new BookingListItem();
						bookingListItem.setId(new ObjectId());
						Optional<Treatment> treatmentOptional = treatmentRepository.findById(new ObjectId(newBookingItemModel.getId()));
						treatmentOptional.ifPresent(treatment -> {
							bookingListItem.setTreatmentQuantity(newBookingItemModel.getQuantity());
							bookingListItem.setTreatment(treatment);

							bookingListItemRepository.save(bookingListItem);
						});
						return bookingListItem;
					})
					.collect(Collectors.toList());

			bookingList.setBookingListItems(bookingListItems);

			bookingListRepository.save(bookingList);

			booking.setCreatedBy(bookingCreatedBy);
			booking.setDateCreated(DateTime.now().getMillis());
			booking.setBookingList(bookingList);
			booking.setDuration(bookingDuration.get());
			booking.setStartDateTime(proposedStartDateTime.getMillis());
			booking.setEndDateTime(proposedEndDateTime.getMillis());

			bookingRepository.save(booking);
		} else {
			DateTime proposedEndDateTime = proposedStartDateTime.plusMinutes(1);

			booking.setClient(client.get());
			booking.setEmployee(employee.get());

			BookingList bookingList = new BookingList();
			bookingList.setId(new ObjectId());

			List<BookingListItem> bookingListItems = new ArrayList<>();
			bookingList.setBookingListItems(bookingListItems);

			booking.setCreatedBy(bookingCreatedBy);
			booking.setDateCreated(DateTime.now().getMillis());
			booking.setBookingList(bookingList);
			booking.setDuration(1);
			booking.setStartDateTime(proposedStartDateTime.getMillis());
			booking.setEndDateTime(proposedEndDateTime.getMillis());

			bookingRepository.save(booking);
		}

		return booking;
	}

	private void bookingIsWithinThreeMonthRange(DateTime startDateTime) {
		DateTime start = new DateTime(startDateTime);

		if(start.isAfter(DateTime.now().plusMonths(2))) {
			throw new InvalidBookingException("The company only allows bookings within 3 months of from the current day. Please chat to the team to book ahead for later dates.");
		}
	}

	private void bookingIsNotToOld(DateTime startDateTime) {
		DateTime start = new DateTime(startDateTime);

		if(start.isBeforeNow()){
			throw new InvalidBookingException("Please ensure the booking is made a for a future date and time.");
		}
	}

	private void bookingIsGivenProperNotice(DateTime startDateTime, ApplicationConfigurationModel applicationConfigurationModel) {
		if(!startDateTime.isAfter(DateTime.now().plusMinutes(applicationConfigurationModel.getBookingNotice()))){
			throw new InvalidBookingException("Please ensure you have provided more than " + applicationConfigurationModel.getBookingNotice() + " minutes notice before your booking.");
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
