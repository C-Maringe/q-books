package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.CompanyRevenueGoal;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRevenueGoalRepository extends MongoRepository<CompanyRevenueGoal, ObjectId> {
    @NotNull
    List<CompanyRevenueGoal> findAll();
    Optional<CompanyRevenueGoal> findByGoalActive(boolean goalActive);
    List<CompanyRevenueGoal> findAllByGoalActive(boolean goalActive);
    List<CompanyRevenueGoal> findAllByGoalActiveAndGoalMeasureDateBefore(boolean goalActive, long goalMeasureDate);
}
