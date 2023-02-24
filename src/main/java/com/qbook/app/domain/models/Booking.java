package com.qbook.app.domain.models;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(value = "booking_collection")
public class Booking {

	@Id
	@Expose ObjectId id;
	@Expose private long dateCreated = 0L; // initialised to prevent null pointers
	@Expose private long dateCancelled = 0L; // initialised to prevent null pointers
    @Expose private long startDateTime;
    @Expose private long endDateTime;
    @Expose private int duration;
	@Expose private BookingCreatedBy createdBy = BookingCreatedBy.CLIENT; // initialised to prevent null pointers
	@Expose private BookingCancelledBy bookingCancelledBy = BookingCancelledBy.CLIENT; // initialised to prevent null pointers
    @Expose private String bookingStatus = "Active";

	//New Properties to accommodate for blocking out personal days
    @Expose private boolean dayToBlockOut = false;
    @Expose private String blockedDayTitle;
	@Expose private boolean workingDay = true;
	@Expose private String workingDayId;

    @Expose(deserialize = false)
	private BookingList bookingList;

    @Expose(deserialize = false)
	@DBRef
	private Employee employee;

    @Expose(deserialize = false)
	@DBRef
	private Client client;

	@Expose private boolean depositPaid = false;

    @Expose(deserialize = false)
    @DBRef
	private Transaction transaction;

    private List<Product> productsPurchased;
}
