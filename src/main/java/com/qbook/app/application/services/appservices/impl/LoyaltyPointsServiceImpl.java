package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.PushNotificationBodyModel;
import com.qbook.app.application.models.PushNotificationMessageModel;
import com.qbook.app.application.services.appservices.EmailService;
import com.qbook.app.application.services.appservices.LoyaltyPointsService;
import com.qbook.app.application.services.appservices.PushNotificationService;
import com.qbook.app.domain.models.Client;
import com.qbook.app.domain.models.Voucher;
import com.qbook.app.domain.repository.ClientRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

@Log
@Service
@AllArgsConstructor
public class LoyaltyPointsServiceImpl implements LoyaltyPointsService {
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ClientRepository clientRepository;
    private final ApplicationProperties applicationProperties;
    private final EmailService emailService;
    private final PushNotificationService pushNotificationService;
    @Override
    public void addPointsToClientAccount(int pointsToAdd, Client client) {
        executorService.submit(() -> {
            log.log(Level.INFO, "addPointsToClientAccount: start adding loyalty points");
            if (client.getLoyaltyPoints() + pointsToAdd >= 30) {
                client.setLoyaltyPoints((client.getLoyaltyPoints() + pointsToAdd) - 30); // reset points
                client
                        .getVouchers()
                        .add(
                                new Voucher(true, DateTime.now().plusMonths(2).toDate().getTime(), DateTime.now().toDate().getTime(),0L, false, applicationProperties.createRandomCode())
                        );
                emailService.sendClientVoucherReceivedEmail(client);

                sendPushNotification(client);
            } else {
                client.setLoyaltyPoints(client.getLoyaltyPoints() + pointsToAdd);
            }

            clientRepository.save(client);
            log.log(Level.INFO, "addPointsToClientAccount: completed adding loyalty points");
        });
    }

    private void sendPushNotification(final Client client) {
        if(client.getPushNotificationUserDevice() != null &&
                client.getPushNotificationUserDevice().getDeviceToken() != null
        ) {
            final PushNotificationMessageModel notification = new PushNotificationMessageModel();
            notification.setTitle("Loyalty Points Received");
            notification
                    .setBody("Hi " + client.getFirstName() + " " + client.getLastName() + ". You have received a new voucher due to your loyalty points exceeding 30 points.");
            final PushNotificationBodyModel model = new PushNotificationBodyModel();
            model.setNotification(notification);
            pushNotificationService.sendNotification(
                    model, client.getId().toString()
            );
        }
    }

    @Override
    public int viewClientLoyaltyPoints(Client client) {
        return client.getLoyaltyPoints();
    }

    @Override
    public List<Voucher> viewClientVouchers(Client client) {
        return client
                .getVouchers();
    }

    @Override
    public boolean isVoucherRedeemed(Client client, String voucherNumber) {
        return client
                .getVouchers()
                .stream()
                .anyMatch(voucher -> voucher.getVoucherNumber().equals(voucherNumber) && DateTime.now().isBefore(new DateTime(voucher.getRedeemedDate())));
    }
}
