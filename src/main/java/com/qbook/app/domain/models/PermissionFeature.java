package com.qbook.app.domain.models;

public enum PermissionFeature {
    CLIENT_MANAGEMENT("Clients"),
    SCHEDULE("Schedule"),
    SALES("Sales"),
    TREATMENTS("Treatments"),
    BOOKINGS("Bookings"),
    CONFIGURATIONS("Configurations"),
    REPORTING("Reporting"),
    ANALYTICS("Analytics"),
    EMPLOYEES("Employees"),
    MARKETING("Marketing"),
    PRODUCTS("Products"),
    GOALS("Goals");

    private final String name;

    PermissionFeature(String name) {this.name = name;}

    public String getName() {
        return name;
    }
}
