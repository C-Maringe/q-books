package com.qbook.app.utilities.factory;


import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.Notification;
import org.joda.time.DateTime;

public class NotificationFactory {

    public static Notification buildNotification(Booking booking) {
        Notification notification = new Notification();
        notification.setDateTimeSent(DateTime.now().getMillis());
        notification.setMessage("Booking reminder");
        notification.setBooking(booking);

        return notification;
    }
}
