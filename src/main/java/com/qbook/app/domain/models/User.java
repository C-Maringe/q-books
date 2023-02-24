package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {

	@Id
	private ObjectId id;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private boolean isActive = true;
	private String role;
	private Long dateRegistered;
	private boolean receiveMarketingEmails;
	private PlatformUsed platformUsed = PlatformUsed.WEB;

	private ContactDetails contactDetails;

	List<UserPermission> userPermissionList = new ArrayList<>();
}
