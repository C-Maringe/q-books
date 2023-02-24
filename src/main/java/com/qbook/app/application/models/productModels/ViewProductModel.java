package com.qbook.app.application.models.productModels;

import lombok.Data;

@Data
public class ViewProductModel {
    private String id;
    private double price;
    private boolean active;
    private boolean special;
    private long specialEndDate;
    private double specialPrice;
    private String productDescription;
    private String productName;
    private String category;
}
