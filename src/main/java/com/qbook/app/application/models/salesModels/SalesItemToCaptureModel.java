package com.qbook.app.application.models.salesModels;

import lombok.Data;

@Data
public class SalesItemToCaptureModel {
    private String cashUpId;
    private String bookingId;
    private double totalCashPaid;
    private double totalCardPaid;
    private double totalEFTPaid;
    private double totalVoucherPaid;
    private boolean discounted;
    private double discountPercentage;
    private double depositAmount;
    private String voucherNumber;
}
