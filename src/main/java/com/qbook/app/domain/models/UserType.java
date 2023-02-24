package com.qbook.app.domain.models;

public enum UserType {
	EMPLOYEE("employee"),
	ADMIN("admin"),
	CLIENT("client");

	private final String name;

	UserType(String name) {this.name = name;}

	public String getName() {
		return name;
	}
}
