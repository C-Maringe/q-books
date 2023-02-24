package com.qbook.app.domain.models;

import lombok.Getter;

public enum PushNotificationMessageStatus {
    FAILED("Failed"),
    SUCCESSFUL("Successful");

    @Getter
    private String value;

    PushNotificationMessageStatus(final String value) {
        this.value = value;
    }
}
