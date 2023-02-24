package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "permission_collection")
public class Permission {

	@Id
	private ObjectId objectId;
	private PermissionFeature permissionFeature;
}
