package com.qbook.app.application.models.salesModels;

import lombok.Data;

@Data
public class SaleNewBookingItemModel {
	private String id;
	private int quantity;
	private boolean specialOffer;
}
