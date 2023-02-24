package com.qbook.app.application.models.paymentGateway;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentStatusModel {
    private String id;
    private String paymentType;
    private String paymentBrand;
    private String amount;
    private PaymentResultModel result;
    private String buildNumber;
    private String timestamp;
    private String ndc;
    private Boolean transactionApproved = false;
}
