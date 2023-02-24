package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.PaymentTransactionLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaymentTransactionLogRepository extends MongoRepository<PaymentTransactionLog, String> {

    Optional<PaymentTransactionLog> findByCheckoutId(String Id);
}
