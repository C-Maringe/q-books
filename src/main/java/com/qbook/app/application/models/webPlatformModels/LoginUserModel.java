package com.qbook.app.application.models.webPlatformModels;

import lombok.Data;

@Data
public class LoginUserModel {
	private String username;
	private String password;
	private boolean keepMeLoggedIn;
}
