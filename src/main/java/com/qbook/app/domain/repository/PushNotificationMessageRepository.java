package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.PushNotificationMessages;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PushNotificationMessageRepository extends MongoRepository<PushNotificationMessages, ObjectId> {
}

