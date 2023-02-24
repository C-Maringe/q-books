package com.qbook.app.domain.models;


import com.qbook.app.application.configuration.exception.ErrorDetails;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "system_email_collection")
public class SystemEmail {

    @Id
    private ObjectId id;
    private String subject;
    private String body;
    private String recipient;
    private String sender;
    private long dateTimeSent;
    private long dateTimeStart;
    private long dateTimeEnd;
    private boolean sent;
    private ErrorDetails errorDetails;
}
