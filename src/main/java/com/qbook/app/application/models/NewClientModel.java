package com.qbook.app.application.models;

import lombok.Data;

@Data
public class NewClientModel {
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String mobileNumber;
    private String password;
    private String confirmPassword;
}
