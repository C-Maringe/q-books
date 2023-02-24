package com.qbook.app.application.models.salesModels;

import lombok.Getter;

public class SalesCashupCompletedModel {
	@Getter private String message;

	public SalesCashupCompletedModel(String message) {
		this.message = message;
	}
}
