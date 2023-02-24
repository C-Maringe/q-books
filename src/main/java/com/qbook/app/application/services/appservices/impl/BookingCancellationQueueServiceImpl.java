package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.InvalidClientException;
import com.qbook.app.application.configuration.exception.InvalidStartDateException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.BookingCancellationMember;
import com.qbook.app.application.models.BookingCancellationNotificationMessage;
import com.qbook.app.application.models.BookingCancellationQueueMember;
import com.qbook.app.application.models.BookingCancellationQueueModel;
import com.qbook.app.application.services.appservices.BookingCancellationQueueService;
import com.qbook.app.application.services.appservices.EmailService;
import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.BookingCancellationQueue;
import com.qbook.app.domain.models.Client;
import com.qbook.app.domain.repository.BookingCancellationQueueRepository;
import com.qbook.app.domain.repository.ClientRepository;
import com.qbook.app.domain.repository.EmployeeRepository;
import com.qbook.app.utilities.factory.BookingCancellationQueueFactory;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
@Service
@AllArgsConstructor
public class BookingCancellationQueueServiceImpl implements BookingCancellationQueueService {
    private final BookingCancellationQueueRepository bookingCancellationQueueRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;
    private final ApplicationProperties applicationProperties;

    @Override
    public BookingCancellationQueueModel addClientToQueue(BookingCancellationQueueMember bookingCancellationQueueMember) {
        log.log(Level.INFO, "Starting process to add client " + bookingCancellationQueueMember.getClientId() + " to the booking cancellation queue.");

        // check dates are not to old
        DateTime startDate = new DateTime(bookingCancellationQueueMember.getStartDate());

        if(startDate.isBeforeNow()) {
            log.log(Level.WARNING, "We are unable to add you to the cancellation queue. The start date you provided is to far back.");
            throw new InvalidStartDateException("We are unable to add you to the cancellation queue. The start date you provided is to far back.");
        }

        DateTime endDate = new DateTime(bookingCancellationQueueMember.getStartDate());
        if(endDate.isBeforeNow()) {
            log.log(Level.WARNING, "We are unable to add you to the cancellation queue. The end date you provided is to far back.");
            throw new InvalidStartDateException("We are unable to add you to the cancellation queue. The end date you provided is to far back.");
        }

        int queueNumber = (int)(bookingCancellationQueueRepository.count() + 1);
        BookingCancellationQueue bookingCancellationQueue = BookingCancellationQueueFactory.buildBookingCancellationQueue(bookingCancellationQueueMember);
        // find the client to ensure they are valid
        Optional<Client> clientOptional = clientRepository.findById(new ObjectId(bookingCancellationQueueMember.getClientId()));

        if(!clientOptional.isPresent()) {
            log.log(Level.WARNING, "We are unable to add you to the cancellation queue. Please contact your administrator.");
            throw new InvalidClientException("We are unable to add you to the cancellation queue. Please contact your administrator.");
        }
        Client client = clientOptional.get();
        bookingCancellationQueue.setClient(client);
        bookingCancellationQueue.setQueuePosition(queueNumber);

        employeeRepository
                .findById(new ObjectId(bookingCancellationQueueMember.getEmployeeId()))
                        .ifPresent(bookingCancellationQueue::setEmployee);

        bookingCancellationQueueRepository.save(bookingCancellationQueue);

        BookingCancellationQueueModel bookingCancellationQueueModel = new BookingCancellationQueueModel();
        bookingCancellationQueueModel.setMessage("You have successfully been added to the queue. You are number " + queueNumber + " in the queue.");
        bookingCancellationQueueModel.setSuccess(true);

        return bookingCancellationQueueModel;
    }

    @Override
    public void notifyNextPersonInQueue(Booking booking) {
        log.log(Level.INFO, "Starting process to notify next client in booking cancellation queue.");

        // We need to find the next person in the queue waiting for a specific
        // employee or if none listed then just notify next client
        Optional<BookingCancellationQueue> optionalBookingCancellationQueue = bookingCancellationQueueRepository
                .findAllByEmployee(booking.getEmployee(), Sort.by(Sort.Direction.ASC, "startDate"))
                .stream()
                .filter(bookingCancellationQueue -> findFirstMatchingClientInQueue(bookingCancellationQueue, booking))
                .findFirst();

        if(optionalBookingCancellationQueue.isPresent()) {
            BookingCancellationQueue bookingCancellationQueue = optionalBookingCancellationQueue.get();
            sendCancellationNotificationEmail(bookingCancellationQueue, booking);
        } else {
            bookingCancellationQueueRepository
                    .findAll(Sort.by(Sort.Direction.ASC, "startDate"))
                    .stream()
                    .filter(bookingCancellationQueue -> findFirstMatchingClientInQueue(bookingCancellationQueue, booking))
                    .findFirst()
                    .ifPresent(bookingCancellationQueue -> { // send them a notification email
                        sendCancellationNotificationEmail(bookingCancellationQueue, booking);
                    });
        }
    }

    private boolean findFirstMatchingClientInQueue(BookingCancellationQueue bookingCancellationQueue, Booking booking) {
        // find one who fits in the start and end date
        DateTime startDate = new DateTime(bookingCancellationQueue.getStartDate())
                .withHourOfDay(Integer.parseInt(bookingCancellationQueue.getStartTime().substring(0,2)))
                .withMinuteOfHour(Integer.parseInt(bookingCancellationQueue.getStartTime().substring(3,5)))
                .withSecondOfMinute(0);

        DateTime endDate = new DateTime(bookingCancellationQueue.getEndDate())
                .withHourOfDay(Integer.parseInt(bookingCancellationQueue.getEndTime().substring(0,2)))
                .withMinuteOfHour(Integer.parseInt(bookingCancellationQueue.getEndTime().substring(3,5)))
                .withSecondOfMinute(0);

        DateTime bookingStartDateTime = new DateTime(booking.getStartDateTime());
        // make sure the booking cancelled start time is between the cancellation period
        return
                (bookingStartDateTime.isAfter(startDate) && bookingStartDateTime.isBefore(endDate)) ||
                        (bookingStartDateTime.isEqual(startDate) || bookingStartDateTime.isEqual(endDate));
    }

    private void sendCancellationNotificationEmail(BookingCancellationQueue bookingCancellationQueue, Booking booking) {
        log.log(Level.INFO, "Client to notify found in booking cancellation queue." + bookingCancellationQueue.toString());

        DateTime bookingDate = new DateTime(booking.getStartDateTime());

        emailService.sendBookingCancellationQueueClientNotification(
                new BookingCancellationNotificationMessage(
                        bookingCancellationQueue.getClient().getContactDetails().getEmailAddress(),
                        bookingCancellationQueue.getClient().getFirstName() + " " + bookingCancellationQueue.getClient().getLastName(),
                        bookingDate.toString(applicationProperties.getLongDateTimeFormatter()),
                        booking.getEmployee().getFirstName() + " " + booking.getEmployee().getLastName()
                )
        );

        // once done sending remove the booking
        bookingCancellationQueueRepository.delete(bookingCancellationQueue);
    }

    @Override
    public List<BookingCancellationMember> viewAllCancellationQueueClients() {
        return bookingCancellationQueueRepository
                .findAll()
                .stream()
                .map(bookingCancellationQueue -> {
                    DateTime startDate = new DateTime(bookingCancellationQueue.getStartDate())
                            .withHourOfDay(Integer.parseInt(bookingCancellationQueue.getStartTime().substring(0,2)))
                            .withMinuteOfHour(Integer.parseInt(bookingCancellationQueue.getStartTime().substring(3,5)))
                            .withSecondOfMinute(0);

                    DateTime endDate = new DateTime(bookingCancellationQueue.getEndDate())
                            .withHourOfDay(Integer.parseInt(bookingCancellationQueue.getEndTime().substring(0,2)))
                            .withMinuteOfHour(Integer.parseInt(bookingCancellationQueue.getEndTime().substring(3,5)))
                            .withSecondOfMinute(0);

                    return new BookingCancellationMember(
                            startDate.toString(applicationProperties.getLongDateTimeFormatter()),
                            endDate.toString(applicationProperties.getLongDateTimeFormatter()),
                            bookingCancellationQueue.getClient().getFirstName() + " " + bookingCancellationQueue.getClient().getLastName(),
                            bookingCancellationQueue.getClient().getContactDetails().getEmailAddress(),
                            bookingCancellationQueue.getQueuePosition()
                    );
                })
                .collect(Collectors.toList());

    }

    @Override
    public void cleanupOldItemsInQueue() {
        // find all queue end dates prior to current date
        bookingCancellationQueueRepository
                .findAll()
                .stream()
                .filter(bookingCancellationQueue -> new DateTime(bookingCancellationQueue.getEndDate()).isBeforeNow())
                .forEach(bookingCancellationQueueRepository::delete);
    }
}
