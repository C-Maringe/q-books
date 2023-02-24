package com.qbook.app.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Builder
@Data
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransactionLog {
    @Id
    private String id;

    @NonNull
    private String transactionId;

    @NonNull
    private String checkoutId;

    @NonNull
    private String code;

    @NonNull
    private String description;


    @CreatedDate
    private Date createdDate;


    @LastModifiedDate
    private Date lastModifiedDate;


    @Version
    private Long version;
}
