package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "auth_token_collection")
public class AuthToken {

	@Id
	private ObjectId id;

	@Indexed(unique = true)
	private String value;

	public AuthToken(String value) {
		this.value = value;
	}
}
