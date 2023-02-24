package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.DeviceRegistrationModel;
import com.qbook.app.application.models.PushNotificationBodyModel;
import com.qbook.app.application.models.PushNotificationRegisterDeviceModel;

public interface PushNotificationService {
    void registerUserDevice(final PushNotificationRegisterDeviceModel model, final String authToken);//NOPMD
    void sendNotification(final PushNotificationBodyModel model, final String userId);//NOPMD
    DeviceRegistrationModel checkDeviceRegistration(final String authToken);
}
