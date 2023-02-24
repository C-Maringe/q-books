package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(value = "login_sessions_collection")
public class LoginSessions {

	@Id
	private ObjectId id;

	private Date loginDateTime;
	private PlatformUsed platformUsed;
	private String authToken;

	@DBRef
	private Client client;

	@DBRef
	private Employee employee;
}
