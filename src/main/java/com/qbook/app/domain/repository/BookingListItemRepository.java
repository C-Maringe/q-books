package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.BookingListItem;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingListItemRepository extends MongoRepository<BookingListItem, ObjectId> {

}
