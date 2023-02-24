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
@Document(value = "daily_cashup_collection")
public class DailyCashup {

	@Id
	private ObjectId id;

	private Long dateCashingUp;

	private Long dateTimeStarted;
	private Long dateTimeUpdated;
	private Long dateTimeCompleted;

	private boolean started;
	private boolean completed;

	@DBRef
	private List<Sale> sales = new ArrayList<>();

	private double daysTakings;
	private double cashTotal;
	private double cardPaymentTotal;
	private double eftTotal;
	private double otherTotal;
	private double daysProfit;

	@DBRef
	private Employee startedBy;
	@DBRef
	private Employee cashedUpBy;

	private String note;
}
