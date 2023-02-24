package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "feedback_collection")
public class ClientFeedback {

    @Id
    private ObjectId id;
    @DBRef
    private Client client;
    private String feedbackMessage;
    private int rating;
    private long dateCreated;
}
