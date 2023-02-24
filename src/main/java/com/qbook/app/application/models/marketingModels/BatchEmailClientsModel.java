package com.qbook.app.application.models.marketingModels;

import lombok.Data;

import java.util.List;

@Data
public class BatchEmailClientsModel {
	private String batchEmailId;
	private List<Client> clients;
	private boolean resending;
}
