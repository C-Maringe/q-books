package com.qbook.app.application.models.paymentGateway;

import lombok.Data;

@Data
public class PaymentCheckoutModel {
    private PaymentResultModel result;
    private String buildNumber;
    private String timestamp;
    private String ndc;
    private String id;
    private String returningUrl;
}
