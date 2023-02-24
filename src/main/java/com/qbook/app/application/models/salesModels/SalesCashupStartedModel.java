package com.qbook.app.application.models.salesModels;

import lombok.Data;

@Data
public class SalesCashupStartedModel {
	private String message;

	public SalesCashupStartedModel(String message) {
		this.message = message;
	}
}
