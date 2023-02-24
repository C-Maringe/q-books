package com.qbook.app.domain.models;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Document(value = "batch_email_collection")
public class BatchEmail {

    @Id
    @Expose
    private ObjectId id;
    @DBRef
    private List<Client> toEmail = new ArrayList<>();
    private BatchEmailStatus batchEmailStatus;
    private String batchEmailTitle;
    private String batchEmailMessage;
    private Date startDate;
    private Date endDate;
    private String promotionalImageUrl;
    @DBRef
    private BatchEmailMetaInfo batchEmailMetaInfo;
}
