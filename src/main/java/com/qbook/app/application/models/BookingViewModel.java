package com.qbook.app.application.models;

import lombok.Data;

import java.util.List;

@Data
public class BookingViewModel {
	private String startTime;
	private String endTime;
	private String employeeFullName;
	private String bookingId;
	private String depositPaid;
	private String depositAmount;
	private String totalPrice;
	private List<String> treatmentNames;
}
