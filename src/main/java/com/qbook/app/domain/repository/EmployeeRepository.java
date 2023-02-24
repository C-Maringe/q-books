package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.Employee;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends MongoRepository<Employee, ObjectId> {
    Optional<Employee> findByUsername(String username);
    List<Employee> findAllByIsActive(boolean active, Sort sort);
}
