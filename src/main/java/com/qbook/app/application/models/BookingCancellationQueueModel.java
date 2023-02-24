package com.qbook.app.application.models;

import lombok.Data;

@Data
public class BookingCancellationQueueModel {
    private String message;
    private Boolean success;
}
