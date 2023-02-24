package com.qbook.app.application.services.appservices.clients;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qbook.app.application.configuration.exception.FirebaseSendNotificationException;
import com.qbook.app.application.configuration.properties.FirebaseProperties;
import com.qbook.app.application.models.APNConversionResponseModel;
import com.qbook.app.application.models.PushNotificationBodyModel;
import com.qbook.app.application.models.PushNotificationMessageResponseModel;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Log
@Component
@AllArgsConstructor
public class FirebasePushNotificationClient {
    private final FirebaseProperties firebaseProperties;
    private final RestTemplate restTemplate;
    private final Gson gson;

    public PushNotificationMessageResponseModel sendPushNotificationMessage(final PushNotificationBodyModel bodyModel, final Boolean bulkRecipients) {
        log.info("Starting sendPushNotificationMessage() at " + System.currentTimeMillis());
        final String url = firebaseProperties.getUrl();
        final JsonObject notificationBody = new JsonObject();

        if (bulkRecipients) {
            notificationBody.addProperty("registration_ids", bodyModel.getRegistration_ids().toString());
        } else {
            notificationBody.addProperty("to", bodyModel.getTo());//NOPMD
        }

        final JsonObject notification = new JsonObject();
        notification.addProperty("title", bodyModel.getNotification().getTitle());
        notification.addProperty("body", bodyModel.getNotification().getBody());
        notificationBody.add("notification", new Gson().toJsonTree(notification));

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", firebaseProperties.getServerKey());

        final HttpEntity<String> request = new HttpEntity<>(gson.toJson(notificationBody), headers);
        ResponseEntity<PushNotificationMessageResponseModel> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(
                    url,
                    request,
                    PushNotificationMessageResponseModel.class);
        } catch (Exception exception) {//NOPMD
            log.info("Exception " + exception.getMessage());
            throw new FirebaseSendNotificationException("An error occurred while sending message with FCM API. Please contact an administrator.", "Error", exception);
        }

        log.info("Response: " + responseEntity);//NOPMD
        log.info("Response: " + responseEntity.getBody());

        log.info("Completed sendPushNotificationMessage() at " + System.currentTimeMillis());
        return responseEntity.getBody();
    }

    public APNConversionResponseModel convertAPNtoFCM(final String apnsToken) {
        log.info("Starting convertAPNtoFCM() at " + System.currentTimeMillis());
        final String url = firebaseProperties.getBatchImportUrl();
        final JsonObject batchImport = new JsonObject();

        final ArrayList<String> apns = new ArrayList<>();
        apns.add(apnsToken);

        batchImport.addProperty("application", firebaseProperties.getApplication());
        batchImport.addProperty("sandbox", false);
        batchImport.add("apns_tokens", gson.toJsonTree(apns).getAsJsonArray());

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", firebaseProperties.getServerKey());

        final HttpEntity<String> request = new HttpEntity<>(gson.toJson(batchImport), headers);
        ResponseEntity<APNConversionResponseModel> responseEntity;

        try {
            responseEntity = restTemplate.postForEntity(
                    url,
                    request,
                    APNConversionResponseModel.class);
        } catch (HttpClientErrorException exception) {//NOPMD
            final String errorResponse = exception.getResponseBodyAsString();
            log.info("Exception " + exception.getMessage());
            log.info("Response Body " + errorResponse);
            throw new FirebaseSendNotificationException("An error occurred while sending message with FCM API. Please contact an administrator.", "Error", exception);
        }

        log.info("Response: " + responseEntity);
        log.info("Response: " + responseEntity.getBody());

        log.info("Completed convertAPNtoFCM() at " + System.currentTimeMillis());
        return responseEntity.getBody();
    }
}
