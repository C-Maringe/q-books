package com.qbook.app.application.models.clientModels;

import lombok.Getter;

public class ClientDisabledModel {
	@Getter private String message;

	public ClientDisabledModel(String message) {
		this.message = message;
	}
}
