package com.qbook.app.application.models;

import lombok.Data;

@Data
public class DeviceRegistrationModel {
    private String platform;
    private Boolean enableNotification;
    private Boolean deviceSetup;
    private String createdDate;
}
