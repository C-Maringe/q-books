package com.qbook.app.application.services.appservices;


import com.qbook.app.application.models.paymentGateway.PaymentStatusModel;

public interface PaymentServices {
    Boolean getPaymentApproval(String code);

    PaymentStatusModel getPaymentTransactionStatus(String checkoutId, String bookingId);

    void savePaymentTransaction(PaymentStatusModel newTransaction, String bookingId);

    PaymentStatusModel getPaymentTransaction(String checkoutId);

}
