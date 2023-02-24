package com.qbook.app.application.models;

import lombok.Getter;

public class UserForgotPasswordModel {
	@Getter private String message;

	public UserForgotPasswordModel(String message) {
		this.message = message;
	}
}
