package com.qbook.app.application.models.scheduleModels;

import lombok.Data;

@Data
public class ScheduleNewBookingItemModel {
	private String id;
	private int quantity;
	private boolean specialOffer;
}
