package com.qbook.app.domain.repository;


import com.qbook.app.domain.models.LoginSessions;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoginSessionRepository extends MongoRepository<LoginSessions, ObjectId> {

}
