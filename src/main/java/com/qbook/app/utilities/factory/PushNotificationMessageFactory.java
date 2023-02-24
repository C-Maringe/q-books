package com.qbook.app.utilities.factory;

import com.qbook.app.application.models.PushNotificationBodyModel;
import com.qbook.app.application.models.PushNotificationMessageResponseModel;
import com.qbook.app.domain.models.PushNotificationMessageStatus;
import com.qbook.app.domain.models.PushNotificationMessages;
import com.qbook.app.domain.models.PushNotificationRecipientType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@AllArgsConstructor
public class PushNotificationMessageFactory {

    public PushNotificationMessages buildDomainModel(final PushNotificationBodyModel bodyModel, final PushNotificationMessageResponseModel responseModel) {//NOPMD
        final PushNotificationMessages entity = new PushNotificationMessages();
        entity.setCreatedDate(Instant.now().toEpochMilli());
        entity.setApiResponse(responseModel.getResults().toString());
        entity.setBadge(0);
        entity.setFailure(responseModel.getFailure());
        entity.setSuccess(responseModel.getSuccess());
        entity.setTitle(bodyModel.getNotification().getTitle());
        entity.setMessage(bodyModel.getNotification().getBody());
        entity.setMessageData("none");
        entity.setMessageRead(false);
        entity.setRecipientType(PushNotificationRecipientType.SINGLE);
        if (responseModel.getSuccess().equals(1)) {
            entity.setStatus(PushNotificationMessageStatus.SUCCESSFUL);
        } else {
            entity.setStatus(PushNotificationMessageStatus.FAILED);
        }

        return entity;
    }
}
