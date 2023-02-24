package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "transaction_collection")
public class Transaction {
    @Id
    private ObjectId id;
    private String paymentType;
    private String brand;
    private String transactionId;
    private String providerResultCode;
    private String providerResultDescription;
    private double amount;
    private String ndc;
    private Boolean transactionApproved = false;
    private Boolean saleCaptured = false;
    private Long createdDate;
}
