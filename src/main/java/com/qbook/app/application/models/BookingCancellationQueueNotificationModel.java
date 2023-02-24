package com.qbook.app.application.models;

import com.qbook.app.domain.models.Client;
import lombok.Data;

@Data
public class BookingCancellationQueueNotificationModel {
    private boolean success;
    private String message;
    private Client client;
}
