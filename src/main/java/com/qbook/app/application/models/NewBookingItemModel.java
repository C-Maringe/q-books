package com.qbook.app.application.models;

import lombok.Data;

@Data
public class NewBookingItemModel {
	private String id;
	private int quantity;
	private boolean specialOffer;
}
