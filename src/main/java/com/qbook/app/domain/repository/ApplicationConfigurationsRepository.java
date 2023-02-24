package com.qbook.app.domain.repository;


import com.qbook.app.domain.models.ApplicationConfigurations;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplicationConfigurationsRepository extends MongoRepository<ApplicationConfigurations, ObjectId> {

}
