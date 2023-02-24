package com.qbook.app.application.models.productModels;

import lombok.Data;

import java.util.List;

@Data
public class ProductItemToCaptureModel {
    private String bookingId;
    private List<ProductItemModel> products;
}
