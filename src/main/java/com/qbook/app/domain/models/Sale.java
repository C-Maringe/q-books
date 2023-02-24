package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(value = "sale_collection")
public class Sale {

	@Id
	private ObjectId id;

	@DBRef
	private Employee assistedBy;

	@DBRef
	private Client saleTo;

	private List<CashupItem> cashupItems = new ArrayList<>();

	private long dateTimeOfSale;

	private double totalSalePrice;

	private double totalCashPaid;

	private double totalCardPaid;

	private double totalEFTPaid;

	private double totalVoucherPaid;

	private boolean discounted;

	private double discountPercentage;

	private double depositPaid;

	private String voucherNumber;

	private boolean captured;

	@DBRef
	private Booking booking;
}

