package com.qbook.app.application.models.marketingModels;

import lombok.Data;

@Data
public class BatchEmailClientModel {
	private String clientIds[];
	private String batchEmailId;
}
