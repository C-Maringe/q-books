package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.BookingCancellationQueue;
import com.qbook.app.domain.models.Employee;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookingCancellationQueueRepository extends MongoRepository<BookingCancellationQueue, ObjectId> {
    List<BookingCancellationQueue> findAllByEmployee(Employee employee, Sort sort);
}
