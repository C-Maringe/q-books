package com.qbook.app.application.tasks;

import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.PushNotificationBodyModel;
import com.qbook.app.application.models.PushNotificationMessageModel;
import com.qbook.app.application.services.appservices.PushNotificationService;
import com.qbook.app.domain.models.Client;
import com.qbook.app.domain.repository.BookingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.joda.time.DateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Level;

@Log
@Component
@AllArgsConstructor
public class BookingRemindersPushNotificationProcess {
    private final BookingRepository bookingRepository;
    private final PushNotificationService pushNotificationService;
    private final ApplicationProperties applicationProperties;
    @Scheduled(cron = "0 0 20 * * *", zone = "Africa/Johannesburg") // Run 8 pm everyday
    public void run() {
        log.log(Level.INFO, "Starting process to send booking reminders. Time -> " + System.currentTimeMillis());
        bookingRepository
                .findAllByEndDateTimeLessThanEqualAndStartDateTimeGreaterThanEqualAndBookingStatus(
                        DateTime.now().withTimeAtStartOfDay().plusDays(3).getMillis(),
                        DateTime.now().withTimeAtStartOfDay().plusDays(2).getMillis(),
                        "Active"
                ).forEach(booking -> {
                    if(!booking.isDayToBlockOut() && booking.getClient() != null) {
                        final Client client =  booking.getClient();

                        if(client.getPushNotificationUserDevice() != null &&
                                client.getPushNotificationUserDevice().getDeviceToken() != null
                        ) {
                            final PushNotificationMessageModel notification = new PushNotificationMessageModel();
                            notification.setTitle("Booking Reminder");
                            notification
                                    .setBody("Hi " + client.getFirstName() + " " + client.getLastName() + "\n\n This is a reminder for your booking at " +
                                            new DateTime(booking.getStartDateTime()).toString(applicationProperties.getMobileEndDateTimeFormatter()) +
                                            " with " + booking.getEmployee().getFirstName() + " " + booking.getEmployee().getLastName());
                            final PushNotificationBodyModel model = new PushNotificationBodyModel();
                            model.setNotification(notification);
                            pushNotificationService.sendNotification(
                                model, client.getId().toString()
                            );
                        }
                    }
                });
        log.log(Level.INFO, "Completed process to send booking reminders. Time -> " + System.currentTimeMillis());
    }
}
