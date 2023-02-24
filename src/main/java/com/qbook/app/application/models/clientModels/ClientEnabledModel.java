package com.qbook.app.application.models.clientModels;

import lombok.Getter;

public class ClientEnabledModel {
	@Getter private String message;

	public ClientEnabledModel(String message) {
		this.message = message;
	}
}
