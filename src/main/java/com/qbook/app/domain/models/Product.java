package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "product_collection")
public class Product {

    @Id
    private ObjectId id;
    private String productName;
    private String productDescription;
    private double price;
    private boolean active = false;
    private boolean special = false;
    private double specialPrice = 0.0;
    private Long specialEndDate = 0L;
    private String category;
}
