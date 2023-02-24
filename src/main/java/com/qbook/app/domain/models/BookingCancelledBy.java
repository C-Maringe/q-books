package com.qbook.app.domain.models;

public enum BookingCancelledBy {
    EMPLOYEE("Employee"),
    CLIENT("Client"),
    SYSTEM("System");

    private final String name;

    BookingCancelledBy(String name) {this.name = name;}

    public String getName() {
        return name;
    }
}
