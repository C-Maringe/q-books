package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.NotificationSuccessModel;
import com.qbook.app.application.models.NotifySpecificClientModel;
import com.qbook.app.application.models.PushNotificationBodyModel;
import com.qbook.app.application.models.PushNotificationMessageModel;
import com.qbook.app.application.services.appservices.BookingServices;
import com.qbook.app.application.services.appservices.EmailService;
import com.qbook.app.application.services.appservices.NotificationsService;
import com.qbook.app.application.services.appservices.PushNotificationService;
import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.Client;
import com.qbook.app.domain.models.Notification;
import com.qbook.app.domain.repository.BookingRepository;
import com.qbook.app.domain.repository.NotificationRepository;
import com.qbook.app.utilities.factory.NotificationFactory;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Log
@Service
@AllArgsConstructor
public class NotificationsServiceImpl implements NotificationsService {
    private final NotificationRepository notificationRepository;
    private final BookingServices bookingServices;
    private final EmailService emailService;
    private final BookingRepository bookingRepository;
    private final PushNotificationService pushNotificationService;
    private final ApplicationProperties applicationProperties;
    @Override
    public NotificationSuccessModel sendReminderNotificationForAllClientsForSpecificDay(String date) {
        // validate the date is correct
        bookingServices
                .allBookingsForDate(date) // find all bookings for the date
                .forEach(booking -> {
                    // save notification
                    Notification notification = NotificationFactory.buildNotification(booking);
                    notificationRepository.save(notification);

                    // send emails
                    emailService.sendClientReminderEmail(booking);

                    sendPushNotification(booking);
                });

        return new NotificationSuccessModel(
                "The notifications are being sent out to the clients with bookings on " + date,
                true
        );
    }

    @Override
    public NotificationSuccessModel sendReminderNotificationForToSpecificClientsForSpecificDay(NotifySpecificClientModel notifySpecificClientModel) {
        bookingRepository
                .findById(new ObjectId(notifySpecificClientModel.getBookingId()))
                .ifPresent(booking -> {
                    // save notification
                    Notification notification = NotificationFactory.buildNotification(booking);
                    notificationRepository.save(notification);
                    // send emails
                    emailService.sendClientReminderEmail(booking);

                    sendPushNotification(booking);
                });

        return new NotificationSuccessModel(
                "The notification is being sent out to the client.",
                true
        );
    }

    private void sendPushNotification(final Booking booking) {
        final Client client = booking.getClient();

        if(booking.getClient().getPushNotificationUserDevice() != null &&
                booking.getClient().getPushNotificationUserDevice().getDeviceToken() != null
        ) {
            final PushNotificationMessageModel pushNotification = new PushNotificationMessageModel();
            pushNotification.setTitle("Booking Reminder");
            pushNotification
                    .setBody("Hi " + client.getFirstName() + " " + client.getLastName() + ". This is a reminder for your booking at " +
                            new DateTime(booking.getStartDateTime()).toString(applicationProperties.getLongDateTimeFormatter()) +
                            " with " + booking.getEmployee().getFirstName() + " " + booking.getEmployee().getLastName());
            final PushNotificationBodyModel model = new PushNotificationBodyModel();
            model.setNotification(pushNotification);
            pushNotificationService.sendNotification(
                    model, client.getId().toString()
            );
        }
    }
}
