package com.qbook.app.application.models;

import lombok.Data;

@Data
public class ResetPasswordModel {
	private String token;
	private String password;
	private String confirmPassword;
}
