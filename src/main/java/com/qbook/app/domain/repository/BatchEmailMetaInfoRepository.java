package com.qbook.app.domain.repository;


import com.qbook.app.domain.models.BatchEmailMetaInfo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BatchEmailMetaInfoRepository extends MongoRepository<BatchEmailMetaInfo, ObjectId> {

}
