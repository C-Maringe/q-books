package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.EmployeeType;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EmployeeTypeRepository extends MongoRepository<EmployeeType, ObjectId> {
    Optional<EmployeeType> findByEmployeeType(String employeeType);
}
