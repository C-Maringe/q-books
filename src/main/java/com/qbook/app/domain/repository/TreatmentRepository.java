package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.EmployeeType;
import com.qbook.app.domain.models.Treatment;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TreatmentRepository extends MongoRepository<Treatment, ObjectId> {
    List<Treatment> findAllByEmployeeTypeOrderByTreatmentNameAsc(EmployeeType employeeType);
    List<Treatment> findAllByEmployeeTypeId(ObjectId id, Sort sort);
    List<Treatment> findAll(Sort sort);
}
