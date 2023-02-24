package com.qbook.app.application.configuration.properties;

import lombok.Data;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

@Configuration
@ConfigurationProperties("application")
@Data
public class ApplicationProperties {
    private final double vatAmount = 1.15;
    private final double voucherDiscountAmount = 5;
    private final int loyaltyPointsThreshold = 30;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private final DateTimeFormatter longDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    private final SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
    private final DateTimeFormatter displayShortDateTimeFormatter = DateTimeFormat.forPattern("MMMM yyyy");
    private final DateTimeFormatter displayNormalDateTimeFormatter = DateTimeFormat.forPattern("dd MMMM yyyy");
    private final DateTimeFormatter shortDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private final DateTimeFormatter mobileStartDateTimeFormatter = DateTimeFormat.forPattern("d MMMM, yyyy HH:mm");
    private final DateTimeFormatter mobileEndDateTimeFormatter = DateTimeFormat.forPattern("HH:mm");

    private String baseUrl;
    private String fromAddress;
    private String portalAdminEmailAddress;

    public String createRandomCode(){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZWabcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < 8; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
}
