package com.qbook.app.application.models;

import lombok.Getter;

public class LoggedInClientModel {
	@Getter private String token;
	@Getter private String firstName;
	@Getter private String lastName;

	public LoggedInClientModel(String token, String firstName, String lastName) {
		this.token = token;
		this.firstName = firstName;
		this.lastName = lastName;
	}
}
