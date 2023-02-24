package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "booking_cancellation_queue_collection")
public class BookingCancellationQueue {

    @Id
    private ObjectId id;
    private Long dateAdded;
    private Long startDate;
    private Long endDate;
    private String startTime;
    private String endTime;
    private int queuePosition;
    @DBRef
    private Client client;

    @DBRef
    private Employee employee;
}
