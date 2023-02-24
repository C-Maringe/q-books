package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.*;
import com.qbook.app.application.configuration.exception.scheduleExceptions.MissingEmployeeTypeException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.BookingCreatedModel;
import com.qbook.app.application.models.BookingItemModel;
import com.qbook.app.application.models.NewBookingModel;
import com.qbook.app.application.models.TimeSlotModel;
import com.qbook.app.application.models.configurationModels.ApplicationConfigurationModel;
import com.qbook.app.application.models.employeeModels.EmployeeScheduleModel;
import com.qbook.app.application.models.scheduleModels.ScheduleClientModel;
import com.qbook.app.application.models.scheduleModels.ScheduleNewBookingModel;
import com.qbook.app.application.models.scheduleModels.ScheduleTreatmentModel;
import com.qbook.app.application.services.appservices.ApplicationConfigurationsServices;
import com.qbook.app.application.services.appservices.AuthTokenServices;
import com.qbook.app.application.services.appservices.EmailService;
import com.qbook.app.application.services.appservices.ScheduleServices;
import com.qbook.app.application.services.specifications.BookingValidationService;
import com.qbook.app.domain.models.*;
import com.qbook.app.domain.repository.BookingRepository;
import com.qbook.app.domain.repository.ClientRepository;
import com.qbook.app.domain.repository.EmployeeRepository;
import com.qbook.app.domain.repository.TreatmentRepository;
import com.qbook.app.utilities.factory.ScheduleFactory;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log
@Service
@AllArgsConstructor
public class ScheduleServicesImpl implements ScheduleServices {
	private final ClientRepository clientRepository;
	private final ApplicationConfigurationsServices applicationConfigurationsServices;
	private final EmployeeRepository employeeRepository;
	private final BookingRepository bookingRepository;
	private final TreatmentRepository treatmentRepository;
	private final EmailService emailService;
	private final BookingValidationService bookingValidationService;
	private final AuthTokenServices authTokenServices;
	private final ModelMapper modelMapper;
	private final ScheduleFactory scheduleFactory;
	private final ApplicationProperties applicationProperties;

	@Override
	public List<TimeSlotModel> viewAllTimeSlotsForEmployeeOnDate(String employeeId, String date) {
		return viewAllTimeSlotsForEmployeeOnDateForWeb(employeeId, date);
	}

	@Override
	public List<TimeSlotModel> viewAllTimeSlotsForEmployeeOnDateForWeb(String employeeId, String date) {
		ApplicationConfigurationModel applicationConfigurationModel = applicationConfigurationsServices.viewApplicationConfiguration();

		if(applicationConfigurationModel == null) {
			throw new MissingApplicationConfigurationException("Please ensure you have setup the application configuration. If you are not sure how to do it, please contact our support team.");
		}

		LocalDateTime bookingDateTime = LocalDateTime.parse(date, DateTimeFormat.forPattern("yy-MM-dd"));

		if(bookingDateTime.isAfter(DateTime.now().plusMonths(3).toLocalDateTime())) {
			throw new InvalidDayException("The company only allows bookings within 3 months of from the current day. Please chat to the team to book ahead for later dates.");
		}

		log.info("Passes three month test.");
		String dayOfWeek = bookingDateTime.toString(DateTimeFormat.forPattern("EEEE"));

		WorkingDay workingDay = getWorkingDayConfiguration(dayOfWeek, applicationConfigurationModel);

		// get all bookings for the specific date filter by employee id and get start and end times
		Optional<Employee> employeeOptional = employeeRepository.findById(new ObjectId(employeeId));

		if(!employeeOptional.isPresent()) {
			throw new ResourceNotFoundException("The employee could not be found.");
		}

		log.info("Employee found.");
		List<Booking> allForEmployeeOnDay = bookingRepository.findAllByEmployeeAndBookingStatusAndStartDateTimeBetween(
				employeeOptional.get(),
				"Active",
				bookingDateTime.toDateTime().withTimeAtStartOfDay().toDate().getTime(),
				bookingDateTime.plusDays(1).toDateTime().withTimeAtStartOfDay().toDate().getTime()
		);
		log.info("Found employee bookings for the date.");
		DateTime localCompanyStartTime = DateTime.parse(workingDay.getWorkStartTime(), DateTimeFormat.forPattern("HH:mm"));
		DateTime localCompanyEndTime = DateTime.parse(workingDay.getWorkEndTime(), DateTimeFormat.forPattern("HH:mm"));

		List<TimeSlotModel> timeSlotModels = new ArrayList<>();

		String companyStartTimeString = localCompanyStartTime.toString(DateTimeFormat.forPattern("HH:mm"));

		int totalMinutesInDay = Minutes.minutesBetween(localCompanyStartTime, localCompanyEndTime).getMinutes();
		for(int i = 0; i < (totalMinutesInDay / 5); i++) { // should loop 108 times
			if(i == 0) {
				timeSlotModels.add(new TimeSlotModel(companyStartTimeString, (canSlotBeBooked(allForEmployeeOnDay, companyStartTimeString))));
			} else {
				DateTime timeSlotStart = localCompanyStartTime.plusMinutes(5 * i);
				timeSlotModels.add(
						new TimeSlotModel(timeSlotStart.toString(DateTimeFormat.forPattern("HH:mm")),
										(canSlotBeBooked(allForEmployeeOnDay, timeSlotStart.toString(DateTimeFormat.forPattern("HH:mm"))))
								)
				);
			}
		}

		return timeSlotModels;
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

	private boolean canSlotBeBooked(List<Booking> allForEmployeeOnDay, String timeSlotString) {
		for(Booking booking: allForEmployeeOnDay) {
			LocalTime bookingStartTime = new LocalTime(booking.getStartDateTime());
			LocalTime bookingEndTime = new LocalTime(booking.getEndDateTime());

			LocalTime timeSlot = LocalTime.parse(timeSlotString, DateTimeFormat.forPattern("HH:mm"));
			if(timeSlot.isAfter(bookingStartTime)
					&& timeSlot.isBefore(bookingEndTime)) {
				return false;
			} else if(timeSlot.isEqual(bookingStartTime)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public List<TimeSlotModel> viewAllTimeSlots(String date) {
		ApplicationConfigurationModel applicationConfigurationModel = applicationConfigurationsServices.viewApplicationConfiguration();

		if(applicationConfigurationModel == null) {
			throw new MissingApplicationConfigurationException("Please ensure you have setup the application configuration. If you are not sure how to do it, please contact our support team.");
		}

		LocalDateTime bookingDateTime = LocalDateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd"));

		String dayOfWeek = bookingDateTime.toString(DateTimeFormat.forPattern("EEEE"));

		WorkingDay workingDay = getWorkingDayConfiguration(dayOfWeek, applicationConfigurationModel);

		DateTime localCompanyStartTime = DateTime.parse(workingDay.getWorkStartTime(), DateTimeFormat.forPattern("HH:mm"));
		DateTime localCompanyEndTime = DateTime.parse(workingDay.getWorkEndTime(), DateTimeFormat.forPattern("HH:mm"));

		List<TimeSlotModel> timeSlotModels = new ArrayList<>();

		String companyStartTimeString = localCompanyStartTime.toString(DateTimeFormat.forPattern("HH:mm"));

		// calculate difference in minutes between start and end
		int totalMinutesInDay = Minutes.minutesBetween(localCompanyStartTime, localCompanyEndTime).getMinutes();
		// how many five minute segments
		for(int i = 0; i < (totalMinutesInDay / 5); i++) { // should loop 108 times
			if(i == 0) {
				timeSlotModels.add(new TimeSlotModel(companyStartTimeString, true));
			} else {
				DateTime timeSlotStart = localCompanyStartTime.plusMinutes(5 * i);
				timeSlotModels.add(new TimeSlotModel(timeSlotStart.toString(DateTimeFormat.forPattern("HH:mm")), true));
			}
		}

		return timeSlotModels;
	}

	@Override
	public List<TimeSlotModel> viewAllTimeSlotsForBlockout(String date) {
		ApplicationConfigurationModel applicationConfigurationModel = applicationConfigurationsServices.viewApplicationConfiguration();

		if(applicationConfigurationModel == null) {
			throw new MissingApplicationConfigurationException("Please ensure you have setup the application configuration. If you are not sure how to do it, please contact our support team.");
		}

		WorkingDay workingDay = getWorkingDayConfiguration(date, applicationConfigurationModel);

		DateTime localCompanyStartTime = DateTime.parse(workingDay.getWorkStartTime(), DateTimeFormat.forPattern("HH:mm"));
		DateTime localCompanyEndTime = DateTime.parse(workingDay.getWorkEndTime(), DateTimeFormat.forPattern("HH:mm"));

		List<TimeSlotModel> timeSlotModels = new ArrayList<>();

		String companyStartTimeString = localCompanyStartTime.toString(DateTimeFormat.forPattern("HH:mm"));

		// calculate difference in minutes between start and end
		int totalMinutesInDay = Minutes.minutesBetween(localCompanyStartTime, localCompanyEndTime).getMinutes();
		// how many five minute segments
		for(int i = 0; i < (totalMinutesInDay / 5); i++) { // should loop 108 times
			if(i == 0) {
				timeSlotModels.add(new TimeSlotModel(companyStartTimeString, true));
			} else {
				DateTime timeSlotStart = localCompanyStartTime.plusMinutes(5 * i);
				timeSlotModels.add(new TimeSlotModel(timeSlotStart.toString(DateTimeFormat.forPattern("HH:mm")), true));
			}
		}

		return timeSlotModels;
	}

	@Override
	public List<EmployeeScheduleModel> getAllActiveEmployeesForSchedule() {
		return
				employeeRepository
						.findAllByIsActive(true, Sort.by(new Sort.Order(Sort.Direction.ASC, "firstName")))
						.stream()
						.filter(employee -> !employee.getRole().equals("admin"))
						.map(employee -> {
							EmployeeScheduleModel employeeScheduleModel = new EmployeeScheduleModel();
							employeeScheduleModel.setEmployeeFullName(employee.getFirstName() + " " + employee.getLastName());
							employeeScheduleModel.setEmployeeId(employee.getId().toString());
							employeeScheduleModel.setEmployeeTitle(employee.getEmployeeType().getEmployeeType());
							return employeeScheduleModel;
						})
						.collect(Collectors.toList());
	}

	@Override
	public List<BookingItemModel> viewAllBookingItemsPerEmployee(String employeeId) {
		Optional<Employee> toBookWith = employeeRepository.findById(new ObjectId(employeeId));

		if(!toBookWith.isPresent()) {
			throw new InvalidEmployeeException("We could not find the the employee provided. Please contact the company to enquire.");
		}

		return treatmentRepository
				.findAllByEmployeeTypeOrderByTreatmentNameAsc(toBookWith.get().getEmployeeType())
				.stream()
				.filter(Treatment::isActive)
				.map(scheduleFactory::buildBookingItemModel)
				.collect(Collectors.toList());
	}

	@Override
	public BookingCreatedModel createBookingForClient(String clientId, NewBookingModel newBookingModel) {
		Booking booking = bookingValidationService.validateBookingAndCreate(authTokenServices.extractUserId(clientId), newBookingModel, BookingCreatedBy.CLIENT);

		final BookingCreatedModel bookingCreatedModel = new BookingCreatedModel();

		int loyaltyPointsToBeEarned = calculateLoyaltyPoints(booking);
		int currentLoyaltyPoints = booking.getClient().getLoyaltyPoints();
		int points = Math.max(applicationProperties.getLoyaltyPointsThreshold() - (currentLoyaltyPoints + loyaltyPointsToBeEarned), 0);
		bookingCreatedModel.setPointsEarned(loyaltyPointsToBeEarned);
		bookingCreatedModel.setPointsNeededForDiscount(points);

		if(newBookingModel.isDepositRequired()) {
			bookingCreatedModel.setMessage("You're booking slot was successfully reserved, once the deposit is paid your booking will be finalised.");
			bookingCreatedModel.setBookingId(booking.getId().toString());

			return bookingCreatedModel;
		} else {
			bookingCreatedModel.setMessage("You're booking was made successfully, you will receive a notification email as proof of booking.");

			emailService.sendBookingEmail(booking);
			emailService.sendBookingEmailToEmployee(booking);

			return bookingCreatedModel;
		}
	}

	@Override
	public BookingCreatedModel createBookingForClientByEmployee(ScheduleNewBookingModel scheduleNewBookingModel) {
		Booking booking = bookingValidationService.validateBookingAndCreate(scheduleNewBookingModel.getClientId(), modelMapper.map(scheduleNewBookingModel, NewBookingModel.class), BookingCreatedBy.EMPLOYEE);

		final BookingCreatedModel bookingCreatedModel = new BookingCreatedModel();

		int loyaltyPointsToBeEarned = calculateLoyaltyPoints(booking);
		int currentLoyaltyPoints = booking.getClient().getLoyaltyPoints();
		int points = Math.max(applicationProperties.getLoyaltyPointsThreshold() - (currentLoyaltyPoints + loyaltyPointsToBeEarned), 0);
		bookingCreatedModel.setPointsEarned(loyaltyPointsToBeEarned);
		bookingCreatedModel.setPointsNeededForDiscount(points);

		if(scheduleNewBookingModel.isDepositRequired()) {
			bookingCreatedModel.setMessage("You're booking slot was successfully reserved, once the deposit is paid your booking will be finalised.");
			bookingCreatedModel.setBookingId(booking.getId().toString());
			return bookingCreatedModel;
		} else {
			bookingCreatedModel.setMessage("You're booking was made successfully, you will receive a notification email as proof of booking.");
			emailService.sendBookingEmail(booking);
			emailService.sendBookingEmailToEmployee(booking);
			return bookingCreatedModel;
		}
	}

	private int calculateLoyaltyPoints(Booking booking) {
		int pointsToAdd = 0;
		double totalBookingValue = booking
				.getBookingList()
				.getBookingListItems()
				.stream()
				.reduce(0.00, (partialResult, bookingListItem) -> partialResult +
								Double.parseDouble(applicationProperties.getDecimalFormat().format((bookingListItem.getTreatment().getSeniorPrice() * bookingListItem.getTreatmentQuantity()))
										.replace(",",".")
								),
						Double::sum);
		if(totalBookingValue > 2001) {
			pointsToAdd = 10;
		} else if(totalBookingValue > 1501 && totalBookingValue <= 2000) {
			pointsToAdd = 9;
		} else if(totalBookingValue > 1001 && totalBookingValue <= 1500) {
			pointsToAdd = 8;
		} else if(totalBookingValue > 851 && totalBookingValue <= 1000) {
			pointsToAdd = 7;
		} else if(totalBookingValue > 701 && totalBookingValue <= 850) {
			pointsToAdd = 6;
		} else if(totalBookingValue > 551 && totalBookingValue <= 700) {
			pointsToAdd = 5;
		} else if(totalBookingValue > 401 && totalBookingValue <= 550) {
			pointsToAdd = 4;
		} else if(totalBookingValue > 251 && totalBookingValue <= 400) {
			pointsToAdd = 3;
		} else if(totalBookingValue > 101 && totalBookingValue <= 250) {
			pointsToAdd = 2;
		} else if(totalBookingValue > 50 && totalBookingValue <= 100) {
			pointsToAdd = 1;
		}

		return pointsToAdd;
	}
	@Override
	public List<ScheduleClientModel> viewClientListForSchedule() {
		return clientRepository
				.findAllByIsActiveOrderByFirstName(true)
				.stream()
				.map(client -> new ScheduleClientModel(client.getId().toString(), client.getFirstName() + " " + client.getLastName()))
				.collect(Collectors.toList());
	}

	@Override
	public List<ScheduleTreatmentModel> viewTreatmentListForScheduleAndEmployeeType(String employeeType) {

		if(employeeType == null) {
			throw new MissingEmployeeTypeException("Please ensure a valid employee is selected.");
		}

		return treatmentRepository
				.findAll(Sort.by(new Sort.Order(Sort.Direction.ASC, "treatmentName")))
				.stream()
				.filter(Treatment::isActive)
				.filter(treatment -> treatment.getEmployeeType().getEmployeeType().equals(employeeType))
				.map(scheduleFactory::buildScheduleTreatmentModel)
				.collect(Collectors.toList());
	}

	@Override
	public List<ScheduleTreatmentModel> viewTreatmentListForScheduleAndEmployeeTypeAndDate(String employeeType, String startDate) {
		if(employeeType == null) {
			throw new MissingEmployeeTypeException("Please ensure a valid employee is selected.");
		}

		LocalDateTime bookingDateTime = LocalDateTime.parse(startDate, DateTimeFormat.forPattern("yy-MM-dd"));

		return treatmentRepository
				.findAll(Sort.by(new Sort.Order(Sort.Direction.ASC, "treatmentName")))
				.stream()
				.filter(Treatment::isActive)
				.filter(treatment -> treatment.getEmployeeType().getEmployeeType().equals(employeeType))
				.map(treatment -> scheduleFactory.buildScheduleTreatmentModel(treatment, bookingDateTime))
				.collect(Collectors.toList());
	}

	@Override
	public List<ScheduleTreatmentModel> viewTreatmentListForSchedule() {
		return treatmentRepository
				.findAll(Sort.by(new Sort.Order(Sort.Direction.ASC, "treatmentName")))
				.stream()
				.filter(Treatment::isActive)
				.map(scheduleFactory::buildScheduleTreatmentModel)
				.collect(Collectors.toList());
	}

}
