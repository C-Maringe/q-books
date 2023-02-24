package com.qbook.app.domain.models;

public enum PlatformUsed {
    WEB("Web"),
    MOBILE("Mobile");

    private final String name;

    PlatformUsed(String name) {this.name = name;}

    public String getName() {
        return name;
    }
}
