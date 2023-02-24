package com.qbook.app.application.configuration.exception;

import com.qbook.app.application.configuration.exception.BusinessException;

public class PaymentStatusException extends BusinessException {
    public PaymentStatusException(String s) {
        super(s, "Payment Error", true);
    }
}
