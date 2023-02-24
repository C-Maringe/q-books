package com.qbook.app.application.models.marketingModels;

import lombok.Data;

@Data
public class BatchEmailModel {
	private String batchEmailId;
	private String batchEmailStatus;
	private String batchEmailTitle;
	private int batchEmailToBeSentTo;
	private String batchEmailActualSentTo;
	private String sendDate;
	private String completedDate;
}
