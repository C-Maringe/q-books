package com.qbook.app.utilities.factory;

import com.mongodb.internal.connection.MultiServerCluster;
import com.qbook.app.application.models.PushNotificationRegisterDeviceModel;
import com.qbook.app.application.models.ReceiveNotification;
import com.qbook.app.domain.models.Client;
import com.qbook.app.domain.models.PushNotificationUserDevice;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@AllArgsConstructor
public class PushNotificationUserDeviceFactory {
    private final ModelMapper modelMapper;

    public PushNotificationUserDevice buildDomainModel(final PushNotificationRegisterDeviceModel bodyModel) {//NOPMD
        final PushNotificationUserDevice entity = modelMapper.map(bodyModel, PushNotificationUserDevice.class);
        entity.setCreatedDate(Instant.now().toEpochMilli());
        return entity;
    }

    public PushNotificationUserDevice setEnableNotification(final Client client, final ReceiveNotification bodyModel) {//NOPMD
        final PushNotificationUserDevice entity = new PushNotificationUserDevice();
        entity.setCreatedDate(Instant.now().toEpochMilli());
        entity.setPlatform(client.getPushNotificationUserDevice().getPlatform());
        entity.setDeviceToken(client.getPushNotificationUserDevice().getDeviceToken());
        entity.setEnableNotification(bodyModel.getEnableNotification());//NOPMD
        entity.setApnsToken(client.getPushNotificationUserDevice().getApnsToken());
        return entity;
    }
}
