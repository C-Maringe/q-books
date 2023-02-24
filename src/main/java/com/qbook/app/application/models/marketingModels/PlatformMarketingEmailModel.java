package com.qbook.app.application.models.marketingModels;

import lombok.Data;

import javax.mail.Address;

@Data
public class PlatformMarketingEmailModel {
    private String subject;
    private String body;
    private Address[] recipients;
}
