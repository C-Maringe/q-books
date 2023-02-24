package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "notification_collection")
public class Notification {

    @Id
    private ObjectId id;
    private long dateTimeSent;
    private String message;

    @DBRef
    private Booking booking;

}
