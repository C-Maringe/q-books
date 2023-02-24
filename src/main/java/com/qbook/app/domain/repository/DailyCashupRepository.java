package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.DailyCashup;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DailyCashupRepository extends MongoRepository<DailyCashup, ObjectId> {
    List<DailyCashup> findAllByDateCashingUpBetweenAndCompletedOrderByDateTimeCompletedDesc(Long from, Long to, boolean completed);
    Optional<DailyCashup> findByDateCashingUp(Long dateCashingUp);
}
