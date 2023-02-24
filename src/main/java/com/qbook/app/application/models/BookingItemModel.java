package com.qbook.app.application.models;

import lombok.Data;

@Data
public class BookingItemModel {

	private String id;
	private String name;
	private String description;
	private int duration;
	private double price;
	private boolean specialOffer;
}
