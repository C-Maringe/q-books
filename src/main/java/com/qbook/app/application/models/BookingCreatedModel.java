package com.qbook.app.application.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class BookingCreatedModel {
	private String message;
	private String bookingId;
	private int pointsEarned;
	private int pointsNeededForDiscount;
}
