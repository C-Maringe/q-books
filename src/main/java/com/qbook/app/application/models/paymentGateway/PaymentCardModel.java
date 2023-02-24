package com.qbook.app.application.models.paymentGateway;

import lombok.Data;

@Data
public class PaymentCardModel {
    private String bin;
    private String last4Digits;
    private String holder;
    private String expiryMonth;
    private String expiryYear;
}
