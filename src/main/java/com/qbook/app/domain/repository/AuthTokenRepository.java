package com.qbook.app.domain.repository;


import com.qbook.app.domain.models.AuthToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthTokenRepository extends MongoRepository<AuthToken, ObjectId> {

}
