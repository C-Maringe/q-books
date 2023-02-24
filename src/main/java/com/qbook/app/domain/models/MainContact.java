package com.qbook.app.domain.models;

import lombok.Getter;

public class MainContact {

    @Getter private String personName;
    @Getter private String personTel;
    @Getter private String personEmail;


    public MainContact(String personName, String personTel, String personEmail) {
        this.personName = personName;
        this.personTel = personTel;
        this.personEmail = personEmail;
    }
}
