package com.qbook.app.application.models.paymentGateway;

import lombok.Data;

import java.util.List;

@Data
public class PaymentResultCode {
    private List<String> successfull;
}
