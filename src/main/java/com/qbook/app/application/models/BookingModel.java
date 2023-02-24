package com.qbook.app.application.models;

import lombok.Data;

@Data
public class BookingModel {
    private String clientFullName;
    private String clientEmail;
    private String bookingSlot;
    private String treatments;
    private String bookingId;
    private boolean notificationSentAlready;
    private boolean depositPaid;

    private String employee;

    public BookingModel(String clientFullName, String clientEmail, String bookingSlot, String treatments, String bookingId, String employee, boolean notificationSentAlready, boolean depositPaid) {
        this.clientFullName = clientFullName;
        this.clientEmail = clientEmail;
        this.bookingSlot = bookingSlot;
        this.treatments = treatments;
        this.bookingId = bookingId;
        this.notificationSentAlready = notificationSentAlready;
        this.depositPaid = depositPaid;
        this.employee = employee;
    }
}
