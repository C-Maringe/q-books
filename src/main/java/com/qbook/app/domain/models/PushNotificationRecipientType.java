package com.qbook.app.domain.models;

import lombok.Getter;

public enum PushNotificationRecipientType {
    SINGLE("Single"),
    BULK("Bulk");

    @Getter
    private String value;

    PushNotificationRecipientType(final String value) {
        this.value = value;
    }
}
