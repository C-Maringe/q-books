package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.Employee;
import com.qbook.app.domain.models.Sale;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SaleRepository extends MongoRepository<Sale, ObjectId> {
    Optional<Sale> findByBooking(Booking booking);
    List<Sale> findAllByAssistedByAndDateTimeOfSaleBetween(Employee assistedBy, Long from, Long to);
    List<Sale> findAllByDateTimeOfSaleBetween(Long from, Long to);
}
