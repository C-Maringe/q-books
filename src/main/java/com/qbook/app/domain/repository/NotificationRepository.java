package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.EmployeeType;
import com.qbook.app.domain.models.Notification;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {
    Optional<Notification> findByBooking(Booking booking);

}
