package com.qbook.app.domain.models;


import lombok.Data;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(value = "client_collection")
public class Client extends User {

    @DBRef
    private List<Booking> booking = new ArrayList<>();
    private long dateOfBirth;
    private boolean hasGivenFeedback = false;
    private boolean hasAcceptedTermsAndAgreements = false;
    private List<Note> notes = new ArrayList<>();
    private int loyaltyPoints = 0;
    private List<Voucher> vouchers = new ArrayList<>();
    private PushNotificationUserDevice pushNotificationUserDevice;
}
