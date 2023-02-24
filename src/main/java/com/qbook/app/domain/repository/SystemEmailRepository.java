package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.SystemEmail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SystemEmailRepository extends MongoRepository<SystemEmail, ObjectId> {

}
