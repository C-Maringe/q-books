package com.qbook.app.domain.models;

public enum CashupItemType {
	BOOKING("Booking"),
	PRODUCT("Product");

	private final String name;

	CashupItemType(String name) {this.name = name;}

	public String getName() {
		return name;
	}
}
