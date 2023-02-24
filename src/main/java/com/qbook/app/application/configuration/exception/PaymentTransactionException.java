package com.qbook.app.application.configuration.exception;

public class PaymentTransactionException extends BusinessException {
    public PaymentTransactionException(String s) {
        super(s, "Payment Transaction Error", false);
    }
}
