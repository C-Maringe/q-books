package com.qbook.app.application.models.webPlatformModels;

import lombok.Data;

@Data
public class UpdateBookingModel {
    private String bookingId;
    private boolean successful;
    private String paymentType;
    private String currency;
    private String merchantTransactionId;
    private String transactionId;
    private String providerResultCode;
    private String providerResultDescription;
    private String descriptor;
    private double amount;
    private String ndc;
}
