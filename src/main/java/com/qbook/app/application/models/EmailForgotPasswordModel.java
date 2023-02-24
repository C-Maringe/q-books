package com.qbook.app.application.models;

import lombok.Data;

@Data
public class EmailForgotPasswordModel {
	private String fullName;
	private String emailAddress;
	private String token;
}
