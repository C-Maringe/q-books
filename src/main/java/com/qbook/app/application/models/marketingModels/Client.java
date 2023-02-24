package com.qbook.app.application.models.marketingModels;

import lombok.Data;

@Data
public class Client {
	private String fullName;
	private String emailAddress;
	private boolean existsInList;
	private String userId;
}
