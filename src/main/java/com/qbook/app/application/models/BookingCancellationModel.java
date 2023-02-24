package com.qbook.app.application.models;

import lombok.Data;

@Data
public class BookingCancellationModel {
    private boolean success;
    private String message;

    public BookingCancellationModel(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
