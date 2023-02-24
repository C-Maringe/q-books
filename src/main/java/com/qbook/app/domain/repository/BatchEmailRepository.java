package com.qbook.app.domain.repository;


import com.qbook.app.domain.models.BatchEmail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BatchEmailRepository extends MongoRepository<BatchEmail, ObjectId> {

}
