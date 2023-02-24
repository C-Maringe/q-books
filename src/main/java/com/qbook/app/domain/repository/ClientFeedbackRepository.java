package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.ClientFeedback;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientFeedbackRepository extends MongoRepository<ClientFeedback, ObjectId> {

}
