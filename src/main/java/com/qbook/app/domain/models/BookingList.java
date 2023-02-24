package com.qbook.app.domain.models;


import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(value = "booking_list_collection")
public class BookingList {
    @Id
    private ObjectId id;

    @DBRef
    private List<BookingListItem> bookingListItems = new ArrayList<>();
}
