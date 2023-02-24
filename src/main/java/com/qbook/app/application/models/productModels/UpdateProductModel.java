package com.qbook.app.application.models.productModels;

import lombok.Data;

@Data
public class UpdateProductModel {
    private double price;
    private boolean special;
    private long specialEndDate;
    private double specialPrice;
    private String productDescription;
    private String productName;
    private String category;
}
