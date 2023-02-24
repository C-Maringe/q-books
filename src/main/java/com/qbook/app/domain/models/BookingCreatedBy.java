package com.qbook.app.domain.models;

public enum BookingCreatedBy {
    EMPLOYEE("Employee"),
    CLIENT("Client"),
    SYSTEM("System");

    private final String name;

    BookingCreatedBy(String name) {this.name = name;}

    public String getName() {
        return name;
    }
}
