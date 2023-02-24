package com.qbook.app.application.models.salesModels;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SalesBookingModel {
    private String clientFullName;
    private String clientEmail;
    private String bookingSlot;
    private String treatments;
    private String bookingId;
    private Double bookingTotal;
    private boolean captured;
    private List<ProductModel> productModels;
}
