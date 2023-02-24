package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by ironhulk on 10/11/2015.
 */
@Data
@Document(value = "booking_list_item_collection")
public class BookingListItem {

    @Id
    private ObjectId id;

    @DBRef
    private Treatment treatment;
    private int treatmentQuantity;

    @DBRef
    private SpecialPackage specials;
    private int specialQuantity;
}
