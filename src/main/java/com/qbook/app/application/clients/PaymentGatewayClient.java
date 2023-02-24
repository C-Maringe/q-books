package com.qbook.app.application.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qbook.app.application.configuration.exception.PaymentStatusException;
import com.qbook.app.application.configuration.exception.PaymentTransactionException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.configuration.properties.PaymentGatewayProperties;
import com.qbook.app.application.models.paymentGateway.PaymentCheckoutModel;
import com.qbook.app.application.models.paymentGateway.PaymentStatusModel;
import com.qbook.app.application.models.paymentGateway.PaymentTransactionModel;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

@Log
@Component
@AllArgsConstructor
public class PaymentGatewayClient {
    private final RestTemplate restTemplate;
    private final PaymentGatewayProperties paymentGatewayProperties;
    private final ObjectMapper objectMapper;
    private final ApplicationProperties applicationProperties;

    public PaymentCheckoutModel preparePaymentCheckout(String price) {
        log.info("Starting preparePaymentCheckout() at " + System.currentTimeMillis());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + paymentGatewayProperties.getAuthCode());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<HashMap<String, String>> request = new HttpEntity(headers);


        ResponseEntity<String> paymentCheckoutResponseEntity = null;
        PaymentCheckoutModel response = null;
        try {
            log.info("Starting preparePaymentCheckout() at " + System.currentTimeMillis());
            String builder = paymentGatewayProperties.getBaseUrl() + "/checkouts?entityId=" +
                    URLEncoder.encode(paymentGatewayProperties.getEntityId(), StandardCharsets.UTF_8.toString()) +
                    "&amount=" +
                    URLEncoder.encode(price, StandardCharsets.UTF_8.toString()) +
                    "&currency=" +
                    URLEncoder.encode("ZAR", StandardCharsets.UTF_8.toString()) +
                    "&paymentType=" +
                    URLEncoder.encode("DB", StandardCharsets.UTF_8.toString()) +
                    "&merchantTransactionId=" +
                    URLEncoder.encode(applicationProperties.createRandomCode(), StandardCharsets.UTF_8.toString());

            URI uri = URI.create(builder);

            log.info("URL: " + uri.toString());
            paymentCheckoutResponseEntity = restTemplate.postForEntity(
                    uri,
                    request,
                    String.class);

            response = objectMapper.readValue(paymentCheckoutResponseEntity.getBody(), PaymentCheckoutModel.class);
            response.setReturningUrl(applicationProperties.getBaseUrl());
        } catch (IOException exception) {
            log.severe(exception.getLocalizedMessage());
            log.info("Exception " + exception.getMessage());
            throw new PaymentStatusException("Unable to create checkout ID.");
        }
        
        log.info("Response: " + paymentCheckoutResponseEntity);
        log.info("Response: " + paymentCheckoutResponseEntity.getBody());
        log.info("Completed preparePaymentCheckout() at " + System.currentTimeMillis());

        return response;

    }

    public PaymentStatusModel getPaymentStatus(String checkoutId) {
        log.info("Starting getPaymentStatus() at " + System.currentTimeMillis());

        String url = paymentGatewayProperties.getBaseUrl() + "/checkouts/" + checkoutId + "/payment?entityId=" + paymentGatewayProperties.getEntityId();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + paymentGatewayProperties.getAuthCode());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity requestHttpEntity = new HttpEntity(headers);

        ResponseEntity<String> paymentStatusResponseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestHttpEntity,
                String.class
        );

        PaymentStatusModel response = null;
        try {
            response = objectMapper.readValue(paymentStatusResponseEntity.getBody(), PaymentStatusModel.class);

        } catch (IOException exception) {
            log.info("Exception " + exception.getMessage());
            throw new PaymentStatusException("Unable to get payment status.");
        }
        log.info("Response: " + paymentStatusResponseEntity);
        log.info("Response: " + paymentStatusResponseEntity.getBody());
        log.info("Completed getPaymentStatus() at " + System.currentTimeMillis());

        return response;
    }
    public PaymentTransactionModel getPaymentTransaction(String uniqueId) {
        log.info("Starting getPaymentTransaction() at " + System.currentTimeMillis());

        String url = paymentGatewayProperties.getBaseUrl() + "/payments/" + uniqueId;

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("authentication.userId", paymentGatewayProperties.getUserId())
                .queryParam("authentication.password", paymentGatewayProperties.getPassword())
                .queryParam("authentication.entityId", paymentGatewayProperties.getEntityId());


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + paymentGatewayProperties.getAuthCode());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<String> paymentTransactionModel = restTemplate.getForEntity(
                builder.toUriString(),
                String.class);


        PaymentTransactionModel response = null;
        try {
            response = objectMapper.readValue(paymentTransactionModel.getBody(), PaymentTransactionModel.class);

        } catch (IOException exception) {
            log.info("Exception " + exception.getMessage());
            throw new PaymentTransactionException("Unable to get payment transaction.");
        }


        log.info("Response: " + paymentTransactionModel);
        log.info("Response: " + paymentTransactionModel.getBody());
        log.info("Completed getPaymentTransaction() at " + System.currentTimeMillis());

        return response;
    }

}
