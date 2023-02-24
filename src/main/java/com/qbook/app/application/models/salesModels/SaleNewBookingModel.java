package com.qbook.app.application.models.salesModels;

import lombok.Data;

@Data
public class SaleNewBookingModel {
	private String clientId;
	private String employeeId;
	private String startDateTime;
	private SaleNewBookingItemModel saleNewBookingItemModels[];
}
