package com.qbook.app.domain.models;

import lombok.Data;


@Data
public class PushNotificationUserDevice {
    private String deviceToken;
    private String platform;
    private Boolean enableNotification;
    private String apnsToken;
    private Long createdDate;
}
