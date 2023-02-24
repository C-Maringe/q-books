package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.Transaction;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TransactionRepository extends MongoRepository<Transaction, ObjectId> {
    Optional<Transaction> findByTransactionId(String transactionId);
}
