package com.qbook.app.application.models.marketingModels;


import lombok.Getter;

public class BatchEmailSentModel {
	@Getter private String message;
	@Getter private boolean success;

	public BatchEmailSentModel(String message, boolean success) {
		this.message = message;
		this.success = success;
	}
}
