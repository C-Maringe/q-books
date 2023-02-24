package com.qbook.app.application.services.appservices.impl;


import com.qbook.app.application.configuration.exception.InvalidBookingException;
import com.qbook.app.application.configuration.exception.NotAuthorisedException;
import com.qbook.app.application.configuration.exception.ResourceNotFoundException;
import com.qbook.app.application.configuration.exception.bookingsExceptions.BookingNoticePeriodException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.BookingCancellationModel;
import com.qbook.app.application.models.BookingModel;
import com.qbook.app.application.models.BookingViewModel;
import com.qbook.app.application.models.configurationModels.ApplicationConfigurationModel;
import com.qbook.app.application.models.scheduleModels.ScheduleBookingsModels;
import com.qbook.app.application.services.appservices.*;
import com.qbook.app.domain.models.*;
import com.qbook.app.domain.repository.BookingRepository;
import com.qbook.app.domain.repository.ClientRepository;
import com.qbook.app.domain.repository.EmployeeRepository;
import com.qbook.app.domain.repository.NotificationRepository;
import com.qbook.app.utilities.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingServices {
	private final BookingRepository bookingRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final BookingCancellationQueueService bookingCancellationQueueService;
    private final ApplicationConfigurationsServices applicationConfigurationsServices;
    private final EmailService emailService;
	private final NotificationRepository notificationRepository;
    private final AuthTokenServices authTokenServices;
	private final ApplicationProperties applicationProperties;

	@Override
	public BookingCancellationModel cancelBooking(ObjectId id, ObjectId userId) {
		ApplicationConfigurationModel applicationConfigurationModel = applicationConfigurationsServices.viewApplicationConfiguration();

		Optional<Employee> employeeOptional = employeeRepository.findById(userId);

		Optional<Booking> bookingOptional = bookingRepository.findById(id);

		if(!bookingOptional.isPresent()) {
			throw new ResourceNotFoundException("We are unable to find the booking.");
		}

		Booking toBeCancelled = bookingOptional.get();

		if(!employeeOptional.isPresent()) {
            if(!isBookingCancelWithProperNotice(new DateTime(toBeCancelled.getStartDateTime())))
                throw new BookingNoticePeriodException("The booking must be cancelled with "+applicationConfigurationModel.getCancelNotice()+" minutes notice. Please contact administrator.");

			if(!toBeCancelled.getClient().getId().toString().equals(userId.toString())) {
				throw new NotAuthorisedException("Your are not allowed to cancel someone else's booking. Please ask the person who created the booking to cancel.");
			}

			toBeCancelled.setBookingCancelledBy(BookingCancelledBy.CLIENT);
        } else {
			if(isBookingOld(new DateTime(toBeCancelled.getStartDateTime()))) {
				throw new BookingNoticePeriodException("You are not allowed to cancel a booking in the past as it will affect the cash up.");
			}

			toBeCancelled.setBookingCancelledBy(BookingCancelledBy.EMPLOYEE);
		}

		toBeCancelled.setDateCancelled(DateTime.now().getMillis());
		toBeCancelled.setBookingStatus("Cancelled");
		bookingRepository.save(toBeCancelled);

        //check if the cancelled day is a blockout day
        if(toBeCancelled.getBlockedDayTitle() == null){
            DateTime startDateTime = new DateTime(toBeCancelled.getStartDateTime());

            emailService.sendBookingCancellationClientNotification(toBeCancelled.getClient(), startDateTime.toString(applicationProperties.getLongDateTimeFormatter()));
            emailService.sendBookingCancellationEmployeeNotification(toBeCancelled.getEmployee(), toBeCancelled.getClient(), startDateTime.toString(applicationProperties.getLongDateTimeFormatter()));
            bookingCancellationQueueService.notifyNextPersonInQueue(toBeCancelled);

		}
		return new BookingCancellationModel(
				true,
				Constants.BOOKING_CANCELLED
		);
	}

	@Override
	public BookingCancellationModel cancel(ObjectId bookingId, ObjectId clientId) {
		ApplicationConfigurationModel applicationConfigurationModel = applicationConfigurationsServices.viewApplicationConfiguration();

		Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

		if(!bookingOptional.isPresent()) {
			throw new ResourceNotFoundException("We are unable to find the booking.");
		}

		Booking toBeCancelled = bookingOptional.get();

		if(!toBeCancelled.getClient().getId().toString().equals(clientId.toString())) {
			throw new NotAuthorisedException("Your are not allowed to cancel a booking not made by yourself. Please ask the person who created the booking to cancel");
		} else {
			if (!isBookingCancelWithProperNotice(new DateTime(toBeCancelled.getStartDateTime())))
				throw new BookingNoticePeriodException("The booking must be cancelled with " + applicationConfigurationModel.getCancelNotice() + " minutes notice. Please contact administrator.");

			toBeCancelled.setBookingStatus("Cancelled");
			toBeCancelled.setDateCancelled(DateTime.now().getMillis());
			toBeCancelled.setBookingCancelledBy(BookingCancelledBy.CLIENT);
			bookingRepository.save(toBeCancelled);

			DateTime startDateTime = new DateTime(toBeCancelled.getStartDateTime());

			emailService.sendBookingCancellationClientNotification(toBeCancelled.getClient(), startDateTime.toString(applicationProperties.getLongDateTimeFormatter()));
			emailService.sendBookingCancellationEmployeeNotification(toBeCancelled.getEmployee(), toBeCancelled.getClient(), startDateTime.toString(applicationProperties.getLongDateTimeFormatter()));
			bookingCancellationQueueService.notifyNextPersonInQueue(toBeCancelled);

			return new BookingCancellationModel(
					true,
					Constants.BOOKING_CANCELLED
			);
		}
	}

    @Override
    public List<ScheduleBookingsModels> getBookingsBetweenStartAndEnd(Long startTime, Long endTime, String employeeId, String authToken) {
		if(authToken.startsWith("Bearer")){
			authToken = authToken.substring(7);
		}
		else{
			authToken = authToken;
		}
		Optional<Client> clientOptional = clientRepository.findById(new ObjectId(authTokenServices.extractUserId(authToken)));
		Optional<Employee> employeeOptional = employeeRepository.findById(new ObjectId(employeeId));

		if(!employeeOptional.isPresent()) {
			throw new ResourceNotFoundException("We are unable to find the employee.");
		}

	    return bookingRepository
			    .findAllByEndDateTimeLessThanEqualAndStartDateTimeGreaterThanEqualAndBookingStatusAndEmployee(endTime, startTime, "Active", employeeOptional.get())
			    .stream()
			    .map(booking -> {

				    ScheduleBookingsModels scheduleBookingsModels = new ScheduleBookingsModels();

				    // proper title is only visible to employee
				    if(booking.isDayToBlockOut()) {
					    scheduleBookingsModels.setTitle(booking.getBlockedDayTitle());

					    if(!clientOptional.isPresent()) {
						    scheduleBookingsModels.setCanCancel(true);
						    scheduleBookingsModels.setCanView(true);
					    } else {
						    scheduleBookingsModels.setCanCancel(false);
						    scheduleBookingsModels.setCanView(false);
					    }

					    scheduleBookingsModels.setDescription("");
					    scheduleBookingsModels.setClientFullName("");
					    scheduleBookingsModels.setEmployeeFullName("");
				    } else {
					    // only display client names to employees or to the actual person viewing their own bookings
					    if(clientOptional.isPresent()) {
						    if(booking.getClient().getId().equals(clientOptional.get().getId())) {
							    scheduleBookingsModels.setTitle(clientOptional.get().getFirstName() + " " + clientOptional.get().getLastName());
							    scheduleBookingsModels.setDescription(showTreatmentsBooked(booking));
							    scheduleBookingsModels.setClientFullName(booking.getClient().getFirstName() + " " + booking.getClient().getLastName());
							    scheduleBookingsModels.setEmployeeFullName(booking.getEmployee().getFirstName() + " " + booking.getEmployee().getLastName());
							    scheduleBookingsModels.setCanCancel(true);
							    scheduleBookingsModels.setCanView(true);
								scheduleBookingsModels.setDepositPaid(booking.isDepositPaid());
						    } else {
							    scheduleBookingsModels.setTitle("Not Available");
							    scheduleBookingsModels.setDescription("");
							    scheduleBookingsModels.setClientFullName("");
							    scheduleBookingsModels.setEmployeeFullName("");
							    scheduleBookingsModels.setCanCancel(false);
							    scheduleBookingsModels.setCanView(false);
								scheduleBookingsModels.setDepositPaid(booking.isDepositPaid());
						    }
					    } else {
						    scheduleBookingsModels.setTitle(booking.getClient().getFirstName() + " " + booking.getClient().getLastName());
						    scheduleBookingsModels.setDescription(showTreatmentsBooked(booking));
						    scheduleBookingsModels.setClientFullName(booking.getClient().getFirstName() + " " + booking.getClient().getLastName());
						    scheduleBookingsModels.setEmployeeFullName(booking.getEmployee().getFirstName() + " " + booking.getEmployee().getLastName());
						    scheduleBookingsModels.setCanCancel(true);
						    scheduleBookingsModels.setCanView(true);
							scheduleBookingsModels.setDepositPaid(booking.isDepositPaid());
					    }
				    }

				    scheduleBookingsModels.setBookingId(booking.getId().toString());
				    scheduleBookingsModels.setStartTime(applicationProperties.getLongDateTimeFormatter().print(new Date(booking.getStartDateTime()).getTime()));
				    scheduleBookingsModels.setEndTime(applicationProperties.getLongDateTimeFormatter().print(new Date(booking.getEndDateTime()).getTime()));
				    return scheduleBookingsModels;
			    })
			    .collect(Collectors.toList());

    }

	@Override
	public ScheduleBookingsModels viewSpecificBooking(String bookingId, String authorization) {
    	if(!ObjectId.isValid(bookingId)) {
    		throw new InvalidBookingException("We cant seem to find the booking, please contact the administrator.");
	    }

		Optional<Booking> bookingOptional = bookingRepository.findById(new ObjectId(bookingId));

		if(!bookingOptional.isPresent()) {
			throw new ResourceNotFoundException("We are unable to find the booking.");
		}

		Booking booking = bookingOptional.get();

		ScheduleBookingsModels scheduleBookingsModels = new ScheduleBookingsModels();
		if(booking.isDayToBlockOut()) {
			scheduleBookingsModels.setTitle(booking.getBlockedDayTitle());
			scheduleBookingsModels.setDescription("");
			scheduleBookingsModels.setClientFullName("");
			scheduleBookingsModels.setEmployeeFullName("");
		} else {
			scheduleBookingsModels.setTitle(booking.getClient().getFirstName() + " " + booking.getClient().getLastName());
			scheduleBookingsModels.setDescription(showTreatmentsBooked(booking));
			scheduleBookingsModels.setClientFullName(booking.getClient().getFirstName() + " " + booking.getClient().getLastName());
			scheduleBookingsModels.setEmployeeFullName(booking.getEmployee().getFirstName() + " " + booking.getEmployee().getLastName());
		}

		scheduleBookingsModels.setBookingId(booking.getId().toString());
		scheduleBookingsModels.setStartTime(applicationProperties.getLongDateTimeFormatter().print(new Date(booking.getStartDateTime()).getTime()));
		scheduleBookingsModels.setEndTime(applicationProperties.getLongDateTimeFormatter().print(new Date(booking.getEndDateTime()).getTime()));
		scheduleBookingsModels.setCanCancel(true);
		scheduleBookingsModels.setCanView(true);
		scheduleBookingsModels.setDepositPaid(booking.isDepositPaid());

		return scheduleBookingsModels;
	}

	@Override
    public List<BookingModel> getAllBookingsForDate(String date) {
		LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormat.forPattern("yy-MM-dd"));

        return bookingRepository
                .findAllByEndDateTimeLessThanEqualAndStartDateTimeGreaterThanEqualAndBookingStatus(
                		localDateTime.toDateTime().withTimeAtStartOfDay().plusHours(23).getMillis(),
						localDateTime.toDateTime().withTimeAtStartOfDay().getMillis(),
						"Active")
                .stream()
                .filter(booking -> booking.getBlockedDayTitle() == null)
                .map(booking -> {
                    LocalDateTime bookingStartDateTime = new LocalDateTime(booking.getStartDateTime());
                    LocalDateTime bookingEndDateTime = new LocalDateTime(booking.getEndDateTime());

                    // check if client has already received a notification for the day
//                    boolean notificationForBookingSentAlready = wasClientNotifiedForBookingBefore(booking);
					boolean notificationForBookingSentAlready = true;
                    return new BookingModel(
                        booking.getClient().getFirstName() + " " + booking.getClient().getLastName(),
                            booking.getClient().getContactDetails().getEmailAddress(),
                            bookingStartDateTime.toString(applicationProperties.getLongDateTimeFormatter()) + " - " + bookingEndDateTime.toString(applicationProperties.getLongDateTimeFormatter()),
                            stringifyTreatments(booking),
                            booking.getId().toString(),
							booking.getEmployee().getFirstName() + " " + booking.getEmployee().getLastName(),
//							booking.get
							notificationForBookingSentAlready,
							booking.isDepositPaid()
                    );
                })
                .collect(Collectors.toList());
    }

    private boolean wasClientNotifiedForBookingBefore(Booking booking) {
//		System.out.println("running "+notificationRepository.findByBooking(booking));
        return notificationRepository.findByBooking(booking).isPresent();
    }

    @Override
    public List<Booking> allBookingsForDate(String date) {
        return bookingRepository
                .findAllByBookingStatus("Active")
                .stream()
                .filter(booking -> booking.getBlockedDayTitle() == null)
                .filter(booking -> {
                    LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormat.forPattern("yy-MM-dd"));

                    LocalDateTime bookingStartDateTime = new LocalDateTime(booking.getStartDateTime());

                    return localDateTime.toDateTime().withTimeAtStartOfDay().isEqual(bookingStartDateTime.toDateTime().withTimeAtStartOfDay());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingViewModel> allBookingsForDateAndClient(String date, String clientId) {
		Optional<Client> clientOptional = clientRepository.findById(new ObjectId(clientId));

		if(!clientOptional.isPresent()) {
			throw new ResourceNotFoundException("We are unable to find the client.");
		}
        return bookingRepository
                .findAllByBookingStatusAndDayToBlockOutAndClient("Active", false, clientOptional.get())
                .stream()
                .filter(booking -> booking.getBlockedDayTitle() == null)
                .filter(booking -> {
                    LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormat.forPattern("yy-MM-dd"));

                    LocalDateTime bookingStartDateTime = new LocalDateTime(booking.getStartDateTime());

                    return localDateTime.toDateTime().withTimeAtStartOfDay().isEqual(bookingStartDateTime.toDateTime().withTimeAtStartOfDay());
                })
		        .map(this::parseBookingModelToBookingViewModel)
                .collect(Collectors.toList());
    }

	@Override
	public List<BookingViewModel> allBookingsForClient(String clientId) {
		Optional<Client> clientOptional = clientRepository.findById(new ObjectId(clientId));

		if(!clientOptional.isPresent()) {
			throw new ResourceNotFoundException("We are unable to find the client.");
		}
		return bookingRepository
				.findAllByBookingStatusAndDayToBlockOutAndClientOrderByDateCreatedDesc("Active", false, clientOptional.get())
				.stream()
				.filter(booking -> booking.getBlockedDayTitle() == null)
				.map(this::parseBookingModelToBookingViewModel)
				.collect(Collectors.toList());
	}

	@Override
	public List<BookingViewModel> filterBookingsForClient(String clientId, Long startDate, Long endDate, String status) {
		Optional<Client> clientOptional = clientRepository.findById(new ObjectId(clientId));

		if(!clientOptional.isPresent()) {
			throw new ResourceNotFoundException("We are unable to find the client.");
		}

		if("All".equals(status)) {
			return bookingRepository
					.findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndDayToBlockOutAndClientOrderByStartDateTimeDesc(startDate, endDate, false, clientOptional.get())
					.stream()
					.filter(booking -> booking.getBlockedDayTitle() == null)
					.filter(booking -> booking.getBookingStatus().equals("Active"))
					.map(this::parseBookingModelToBookingViewModel)
					.collect(Collectors.toList());
		} else {
			return bookingRepository
					.findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndBookingStatusAndDayToBlockOutAndClientOrderByStartDateTimeDesc(startDate, endDate, status, false, clientOptional.get())
					.stream()
					.filter(booking -> booking.getBlockedDayTitle() == null)
					.filter(booking -> booking.getBookingStatus().equals("Active"))
					.map(this::parseBookingModelToBookingViewModel)
					.collect(Collectors.toList());
		}
	}

	private boolean isBookingCancelWithProperNotice(DateTime start){
		ApplicationConfigurationModel applicationConfigurationModel = applicationConfigurationsServices.viewApplicationConfiguration();
		return (start.isAfter(DateTime.now().plusMinutes(applicationConfigurationModel.getCancelNotice()))); // use configurations here
	}

	private boolean isBookingOld(DateTime start){
		return (start.isBefore(DateTime.now().withTimeAtStartOfDay()));
	}

	private String showTreatmentsBooked(Booking b) {
        if(b.isDayToBlockOut()) {
            return "";
        } else {
            List<BookingListItem> bookingListItems = b.getBookingList().getBookingListItems();

            StringBuilder stringBuilder = new StringBuilder("<br/>");

            for(BookingListItem bookingListItem: bookingListItems) {
                if(bookingListItem.getSpecials() != null) {
                    stringBuilder.append(bookingListItem.getSpecialQuantity()).append(" x ").append(bookingListItem.getSpecials().getSpecialName()).append("<br/>");
                } else {
	                if(bookingListItem.getTreatment().isSpecial() && bookingListItem.getTreatment().getSpecialEndDate() != 0L) {
		                if(bookingListItem.getTreatment().getSpecialEndDate() >= b.getStartDateTime()) {
			                stringBuilder.append("<span style='color: #28a745!important; font-weight: bold'>").append(bookingListItem.getTreatmentQuantity()).append(" x ").append(bookingListItem.getTreatment().getTreatmentName()).append(" (Special)</span><br/>");
		                } else {
			                stringBuilder.append(bookingListItem.getTreatmentQuantity()).append(" x ").append(bookingListItem.getTreatment().getTreatmentName()).append("<br/>");
		                }
	                } else {
		                stringBuilder.append(bookingListItem.getTreatmentQuantity()).append(" x ").append(bookingListItem.getTreatment().getTreatmentName()).append("<br/>");
	                }
                }
            }

            return stringBuilder.toString();
        }
    }

	private String stringifyTreatments(Booking booking) {
        StringBuilder stringBuilder = new StringBuilder("");

        for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
            if(bookingListItem.getSpecials() != null) {
                stringBuilder.append(bookingListItem.getSpecialQuantity()).append(" x ").append(bookingListItem.getSpecials().getSpecialName()).append("<br/>");
            } else {
                stringBuilder.append(bookingListItem.getTreatmentQuantity()).append(" x ").append(bookingListItem.getTreatment().getTreatmentName()).append("<br/>");
            }
        }

        return stringBuilder.toString();
    }

    private BookingViewModel parseBookingModelToBookingViewModel(Booking booking) {
	    BookingViewModel bookingViewModel = new BookingViewModel();
	    bookingViewModel.setEmployeeFullName(booking.getEmployee().getFirstName() + " " + booking.getEmployee().getLastName());
	    bookingViewModel.setBookingId(booking.getId().toString());
		bookingViewModel.setDepositPaid(booking.isDepositPaid()?"Yes":"No");

		if(booking.isDepositPaid()) {
			Transaction transaction = booking.getTransaction();

			if (transaction == null) {
				throw new ResourceNotFoundException("Unable to find the booking transaction.");
			}
			bookingViewModel.setDepositAmount(String.valueOf(transaction.getAmount()));
		}
	    DateTime startTime = new DateTime(booking.getStartDateTime());

	    bookingViewModel.setStartTime(startTime.toString(applicationProperties.getShortDateTimeFormatter()));

	    DateTime endTime = new DateTime(booking.getEndDateTime());
	    bookingViewModel.setEndTime(
				startTime.toString(applicationProperties.getSimpleTimeFormat().toPattern()) + " - " +
	    		endTime.toString(applicationProperties.getSimpleTimeFormat().toPattern()));

	    List<String> treatmentNames = new ArrayList<>();
	    double totalAmount = 0.0;
	    for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
		    if(bookingListItem.getSpecials() != null) {
			    treatmentNames.add(bookingListItem.getSpecials().getSpecialName());

				totalAmount += bookingListItem.getSpecials().getSeniorPrice() * bookingListItem.getSpecialQuantity();
		    } else if(bookingListItem.getTreatment() != null) {
			    treatmentNames.add(bookingListItem.getTreatment().getTreatmentName());

			    if(bookingListItem.getTreatment().isDoneByJunior()) {
					totalAmount += bookingListItem.getTreatment().getJuniorPrice() * bookingListItem.getTreatmentQuantity();
				} else {
					totalAmount += bookingListItem.getTreatment().getSeniorPrice() * bookingListItem.getTreatmentQuantity();
				}

		    }
	    }

		bookingViewModel.setTotalPrice(applicationProperties.getDecimalFormat().format(totalAmount));
	    bookingViewModel.setTreatmentNames(treatmentNames);

	    return bookingViewModel;
    }
}
