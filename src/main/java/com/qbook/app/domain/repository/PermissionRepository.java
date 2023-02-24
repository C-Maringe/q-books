package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.Permission;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PermissionRepository extends MongoRepository<Permission, ObjectId> {

}
