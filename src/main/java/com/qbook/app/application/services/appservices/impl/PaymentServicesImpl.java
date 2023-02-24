package com.qbook.app.application.services.appservices.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qbook.app.application.clients.PaymentGatewayClient;
import com.qbook.app.application.configuration.exception.ResourceNotFoundException;
import com.qbook.app.application.models.paymentGateway.PaymentResultCode;
import com.qbook.app.application.models.paymentGateway.PaymentStatusModel;
import com.qbook.app.application.services.appservices.PaymentServices;
import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.PaymentTransactionLog;
import com.qbook.app.domain.models.Transaction;
import com.qbook.app.domain.repository.BookingRepository;
import com.qbook.app.domain.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Log
@Component
@AllArgsConstructor
public class PaymentServicesImpl implements PaymentServices {

    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;
    private final PaymentGatewayClient paymentGatewayClient;

    /**
     * Purpose of this function is to check if a transaction is approved
     *
     * @param code
     * @return @{@link boolean}
     */
    @Override
    public Boolean getPaymentApproval(final String code) {
        log.info("Starting getPaymentApproval() at " + System.currentTimeMillis());
        Boolean transactionApproved = false;
        try {
            log.info("Starting inputStream mapping at " + System.currentTimeMillis());
            InputStream inputStream = PaymentGatewayClient.class.getResourceAsStream("/reference_data/result-codes.json");
            PaymentResultCode paymentResultCode = objectMapper.readValue(inputStream, PaymentResultCode.class);
            List<String> successfullCodes = paymentResultCode.getSuccessfull();
            log.info("Completed inputStream mapping at " + System.currentTimeMillis());
            transactionApproved = successfullCodes.contains(code);
        } catch (IOException exception) {
            log.info("Exception " + exception.getMessage());
        }
        log.info("Completed getPaymentApproval() at " + System.currentTimeMillis());
        return transactionApproved;
    }


    /**
     * Purpose of this function is to find a transaction from the transaction log table
     *
     * @param checkoutId
     * @param bookingId
     * @return @{@link boolean}
     */
    @Override
    public PaymentStatusModel getPaymentTransactionStatus(final String checkoutId, final String bookingId) {
        log.info("Starting getPaymentStatus() at " + System.currentTimeMillis());
        PaymentStatusModel paymentStatusModel = modelMapper.map(paymentGatewayClient.getPaymentStatus(checkoutId), PaymentStatusModel.class);
        paymentStatusModel.setTransactionApproved(getPaymentApproval(paymentStatusModel.getResult().getCode()));
        savePaymentTransaction(paymentStatusModel, bookingId);
        return paymentStatusModel;

    }

    /**
     * Purpose of this function is to find a transaction from the transaction log table
     *
     * @param newTransaction
     * @param bookingId
     */
    @Override
    public void savePaymentTransaction(final PaymentStatusModel newTransaction, final String bookingId) {
        log.info("Starting savePaymentTransaction() at " + System.currentTimeMillis());
        Optional<Booking> bookingOptional = bookingRepository.findById(new ObjectId(bookingId));

        if(!bookingOptional.isPresent()) {
            throw new ResourceNotFoundException("The booking was not found. Please contact the administrator.");
        }

        Booking booking = bookingOptional.get();

        Transaction transaction = modelMapper.map(newTransaction, Transaction.class);
        transaction.setCreatedDate(DateTime.now().getMillis());
        transaction.setProviderResultCode(newTransaction.getResult().getCode());
        transaction.setProviderResultDescription(newTransaction.getResult().getDescription());
        transaction.setTransactionId(newTransaction.getId());
        Transaction saved = transactionRepository.save(transaction);

        booking.setTransaction(saved);
        booking.setBookingStatus(newTransaction.getTransactionApproved()?"Active":"Cancelled");
        booking.setDepositPaid(true);
        bookingRepository.save(booking);

        log.info("Completed savePaymentTransaction() at " + System.currentTimeMillis());
    }

    /**
     * Purpose of this function is to find a transaction from the transaction log table and map it to transaction model
     *
     * @param checkoutId
     * @return @{@link PaymentStatusModel}
     */
    @Override
    public PaymentStatusModel getPaymentTransaction(final String checkoutId) {
        log.info("Starting getPaymentTransaction() at " + System.currentTimeMillis());
        Optional<Transaction> optionalTransaction = transactionRepository.findByTransactionId(checkoutId);
        log.info("Completed getPaymentTransaction() at " + System.currentTimeMillis());
        return null;
//        if (optionalTransaction.isPresent()) {
//            PaymentStatusModel paymentStatusModel = paymentTransactionLogFactory.buildPaymentStatusModel(paymentTransactionLog.get());
//            return paymentStatusModel;
//        } else {
//            throw new ResourceNotFoundException("The payment transaction could not be found. Please contact the administrator.");
//        }
    }

}
