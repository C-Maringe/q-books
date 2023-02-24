package com.qbook.app.application.models.paymentGateway;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentResultModel {
    private String code;
    private String description;
}
