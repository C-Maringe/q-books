package com.qbook.app.application.models;

import lombok.Data;

@Data
public class BookingCancellationMember {
    private String startDateTime;
    private String endDateTime;
    private String clientFullName;
    private String clientEmail;
    private Integer queuePosition;

    public BookingCancellationMember(String startDateTime, String endDateTime, String clientFullName, String clientEmail, Integer queuePosition) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.clientFullName = clientFullName;
        this.clientEmail = clientEmail;
        this.queuePosition = queuePosition;
    }
}
