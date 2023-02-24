package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class CashupItem {
	private CashupItemType cashupItemType;

	private String itemName;

	private double itemPrice;

	private ObjectId serviceItemId;

	private int duration;

	private int quantity;

	private double totalPrice;

	private boolean special;
}
