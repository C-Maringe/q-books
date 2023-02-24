package com.qbook.app.application.models.marketingModels;

import lombok.Data;

@Data
public class BatchEmailClientSetupModel {
	private String batchEmailId;
	private String batchEmailTitle;
	private String batchEmailMessage;
	private boolean resending;
	private boolean success;

	public BatchEmailClientSetupModel(String batchEmailId, String batchEmailTitle, String batchEmailMessage, boolean resending, boolean success) {
		this.batchEmailId = batchEmailId;
		this.batchEmailTitle = batchEmailTitle;
		this.batchEmailMessage = batchEmailMessage;
		this.resending = resending;
		this.success = success;
	}
}
