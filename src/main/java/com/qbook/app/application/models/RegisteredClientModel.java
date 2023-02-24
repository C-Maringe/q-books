package com.qbook.app.application.models;

import lombok.Getter;

public class RegisteredClientModel {
	@Getter private String token;
	@Getter private String firstName;
	@Getter private String lastName;

	public RegisteredClientModel(String token, String firstName, String lastName) {
		this.token = token;
		this.firstName = firstName;
		this.lastName = lastName;
	}
}
