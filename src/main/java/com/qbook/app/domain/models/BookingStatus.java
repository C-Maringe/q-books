package com.qbook.app.domain.models;

public enum BookingStatus {

	Blocked("Blocked"),
	Cancelled("Cancelled"),
	Completed("Completed"),
	Pending("Pending");

	private final String name;

	BookingStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
