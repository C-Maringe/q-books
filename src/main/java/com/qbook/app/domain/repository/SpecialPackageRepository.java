package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.SpecialPackage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpecialPackageRepository extends MongoRepository<SpecialPackage, ObjectId> {

}
