package com.qbook.app.application.rest.web;

import com.qbook.app.application.clients.PaymentGatewayClient;
import com.qbook.app.application.models.paymentGateway.PaymentCheckoutModel;
import com.qbook.app.application.models.paymentGateway.PaymentStatusModel;
import com.qbook.app.application.models.paymentGateway.PaymentTransactionModel;
import com.qbook.app.application.models.webPlatformModels.PaymentModel;
import com.qbook.app.application.services.appservices.PaymentServices;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;

@Log
@RestController
@RequestMapping(value = "/api/schedule/payments", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class PaymentResource {

    private final PaymentGatewayClient paymentGatewayClient;
    private final PaymentServices paymentServices;

    @PostMapping
    public ResponseEntity<PaymentCheckoutModel> preparePaymentCheckout(@RequestBody PaymentModel paymentModel) {
        log.log(Level.INFO, "Started preparePaymentCheckout. Start time: " + System.currentTimeMillis());
        PaymentCheckoutModel retrievalModal = paymentGatewayClient.preparePaymentCheckout(paymentModel.getPrice());
        log.log(Level.INFO, "Completed preparePaymentCheckout. Start time: " + System.currentTimeMillis());
        return new ResponseEntity<>(retrievalModal, HttpStatus.CREATED);
    }

    @GetMapping("status/{checkoutId}/{bookingId}")
    public ResponseEntity<PaymentStatusModel> getPaymentStatus(
            final @PathVariable String checkoutId,
            final @PathVariable String bookingId
    ) {
        log.log(Level.INFO, "Started getPaymentStatus. Start time: " + System.currentTimeMillis());
        PaymentStatusModel retrievalPaymentStatusModel = paymentServices.getPaymentTransactionStatus(checkoutId, bookingId);
        log.log(Level.INFO, "Completed getPaymentStatus. Start time: " + System.currentTimeMillis());
        return new ResponseEntity<>(retrievalPaymentStatusModel, HttpStatus.OK);
    }

    @GetMapping("transaction/{uniqueId}")
    public ResponseEntity<PaymentTransactionModel> getTransaction(
            final @PathVariable String uniqueId
    ) {
        log.log(Level.INFO, "Started getPaymentTransaction. Start time: " + System.currentTimeMillis());
        PaymentTransactionModel retrievalPaymentTransactionModel = paymentGatewayClient.getPaymentTransaction(uniqueId);
        log.log(Level.INFO, "Completed getPaymentTransaction. Start time: " + System.currentTimeMillis());
        return new ResponseEntity<>(retrievalPaymentTransactionModel, HttpStatus.OK);
    }

}
