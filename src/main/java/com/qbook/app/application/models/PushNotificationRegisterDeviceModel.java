package com.qbook.app.application.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PushNotificationRegisterDeviceModel {
    private String deviceToken;
    private String platform;
    private Boolean enableNotification;
    private String apnsToken;
}
