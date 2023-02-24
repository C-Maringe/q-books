package com.qbook.app.domain.models;

import lombok.Getter;

public class ContactDetails {

    @Getter private String emailAddress;
    @Getter private String mobileNumber;

    public ContactDetails() {
    }

    public ContactDetails(String emailAddress, String mobileNumber) {
        this.emailAddress = emailAddress;
        this.mobileNumber = mobileNumber;
    }
}
