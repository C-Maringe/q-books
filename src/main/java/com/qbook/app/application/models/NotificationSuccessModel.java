package com.qbook.app.application.models;

import lombok.Data;

@Data
public class NotificationSuccessModel {
    private String message;
    private boolean success;

    public NotificationSuccessModel(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}
