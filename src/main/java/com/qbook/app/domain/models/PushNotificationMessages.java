package com.qbook.app.domain.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "push_notification_messages_collection")
public class PushNotificationMessages {
    private static final long serialVersionUID = 1L;
    private String title;
    private String message;
    private PushNotificationMessageStatus status;
    private String messageData;
    private Integer badge;
    private PushNotificationRecipientType recipientType;
    private Integer success;
    private Integer failure;
    private String apiResponse;
    private Boolean messageRead;
    private Long createdDate;
    @DBRef
    private Client client;//NOPMD
}
