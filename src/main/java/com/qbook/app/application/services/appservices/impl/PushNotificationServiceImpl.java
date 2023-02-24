package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.ResourceNotFoundException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.*;
import com.qbook.app.application.services.appservices.AuthTokenServices;
import com.qbook.app.application.services.appservices.PushNotificationService;
import com.qbook.app.application.services.appservices.clients.FirebasePushNotificationClient;
import com.qbook.app.domain.models.Client;
import com.qbook.app.domain.models.PushNotificationMessages;
import com.qbook.app.domain.repository.ClientRepository;
import com.qbook.app.domain.repository.PushNotificationMessageRepository;
import com.qbook.app.utilities.factory.PushNotificationMessageFactory;
import com.qbook.app.utilities.factory.PushNotificationUserDeviceFactory;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log
@Service
@AllArgsConstructor
@Transactional
public class PushNotificationServiceImpl implements PushNotificationService {
    private final ClientRepository clientRepository;
    private final PushNotificationMessageRepository pushNotificationMessageRepository;
    private final PushNotificationUserDeviceFactory pushNotificationUserDeviceFactory;
    private final PushNotificationMessageFactory pushNotificationMessageFactory;
    private final FirebasePushNotificationClient firebasePushNotificationClient;
    private final AuthTokenServices authTokenServices;
    private final ApplicationProperties applicationProperties;

    @Override
    public void registerUserDevice(final PushNotificationRegisterDeviceModel regModel, final String authToken) {
        final Optional<Client> clientOptional = clientRepository.findById(new ObjectId(authTokenServices.extractUserId(authToken)));

        if(!clientOptional.isPresent()) {
            throw new ResourceNotFoundException("We are unable to find the client profile. Please contact the administrator.");
        }

        final Client client = clientOptional.get();

        if (client.getPushNotificationUserDevice() != null) {
            System.out.println("Clearing old device");
            client.setPushNotificationUserDevice(null);
        }

        if("ios".equals(regModel.getPlatform())) {
            final APNConversionResponseModel apnConversionResponseModel = firebasePushNotificationClient.convertAPNtoFCM(regModel.getDeviceToken());
            final Optional<APNConversionResultsModel> resultModel = apnConversionResponseModel.getResults().stream().findFirst();
            if(resultModel.isPresent()) {
                regModel.setDeviceToken(resultModel.get().getRegistration_token());
                regModel.setApnsToken(resultModel.get().getApns_token());
            }
        }

        System.out.println("Updating client device");
        client.setPushNotificationUserDevice(pushNotificationUserDeviceFactory.buildDomainModel(regModel));
        clientRepository.save(client);
    }

    @Override
    public void sendNotification(final PushNotificationBodyModel bodyModel, final String userId) {//NOPMD
        final Optional<Client> clientOptional = clientRepository.findById(new ObjectId(userId));

        if(!clientOptional.isPresent()) {
            throw new ResourceNotFoundException("We are unable to find the user to notify.");
        }

        final Client client = clientOptional.get();
        if (client.getPushNotificationUserDevice() != null) {
            bodyModel.setTo(client.getPushNotificationUserDevice().getDeviceToken());
            final PushNotificationMessageResponseModel pushNotificationMessageResponseModel = firebasePushNotificationClient.sendPushNotificationMessage(bodyModel, false);
            final PushNotificationMessages pushNotificationMessages = pushNotificationMessageFactory.buildDomainModel(bodyModel, pushNotificationMessageResponseModel);
            pushNotificationMessages.setClient(client);
            pushNotificationMessageRepository.save(pushNotificationMessages);
        }
    }

    @Override
    public DeviceRegistrationModel checkDeviceRegistration(String authToken) {
        final Optional<Client> clientOptional = clientRepository.findById(new ObjectId(authTokenServices.extractUserId(authToken)));

        if(!clientOptional.isPresent()) {
            throw new ResourceNotFoundException("We are unable to find the client profile. Please contact the administrator.");
        }

        final Client client = clientOptional.get();
        final DeviceRegistrationModel deviceRegistrationModel = new DeviceRegistrationModel();

        if (client.getPushNotificationUserDevice() != null) {
            deviceRegistrationModel.setEnableNotification(client.getPushNotificationUserDevice().getEnableNotification());
            deviceRegistrationModel.setPlatform(client.getPushNotificationUserDevice().getPlatform());
            deviceRegistrationModel.setDeviceSetup(true);
            deviceRegistrationModel.setCreatedDate(
                    applicationProperties
                            .getLongDateTimeFormatter()
                            .print(client.getPushNotificationUserDevice().getCreatedDate())
            );
        } else {
            deviceRegistrationModel.setDeviceSetup(false);
        }
        return deviceRegistrationModel;
    }
}
