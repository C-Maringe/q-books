package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.NotificationSuccessModel;
import com.qbook.app.application.models.NotifySpecificClientModel;

public interface NotificationsService {

    NotificationSuccessModel sendReminderNotificationForAllClientsForSpecificDay(String date);

    NotificationSuccessModel sendReminderNotificationForToSpecificClientsForSpecificDay(NotifySpecificClientModel notifySpecificClientModel);
}
