package com.qbook.app.application.models.salesModels;

import lombok.Data;

@Data
public class SalesEditBookingModel {
	private String bookingId;
	private String startDateTime;
	private SaleEditBookingItemModel saleEditBookingItemModels[];
}
