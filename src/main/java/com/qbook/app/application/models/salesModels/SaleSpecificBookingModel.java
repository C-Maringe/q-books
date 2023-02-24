package com.qbook.app.application.models.salesModels;

import com.qbook.app.domain.models.Voucher;
import lombok.Data;

import java.util.List;

@Data
public class SaleSpecificBookingModel {
	private String clientId;
	private String clientEmail;
	private String clientFullName;
	private String employeeId;
	private String employeeEmail;
	private String employeeFullName;
	private String bookingDate;
	private String startTime;
	private boolean depositPaid;
	private double depositAmount;
	private List<SalesBookingItemModel> salesBookingItemModels;
	private List<SaleTimeSlotModel> saleTimeSlotModels;
	private Voucher voucher;
	private double totalToPay;
}
