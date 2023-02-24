package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.BookingList;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingListRepository extends MongoRepository<BookingList, ObjectId> {

}
