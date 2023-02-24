package com.qbook.app.application.models.paymentGateway;

import lombok.Data;

@Data
public class PaymentTransactionModel {
    private String id;
    private String paymentType;
    private String paymentBrand;
    private PaymentResultModel result;
    private String buildNumber;
    private String timestamp;
}
