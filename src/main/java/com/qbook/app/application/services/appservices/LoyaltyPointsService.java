package com.qbook.app.application.services.appservices;

import com.qbook.app.domain.models.Client;
import com.qbook.app.domain.models.Voucher;

import java.util.List;

public interface LoyaltyPointsService {
    void addPointsToClientAccount(int pointsToAdd, Client client);

    int viewClientLoyaltyPoints(Client client);

    List<Voucher> viewClientVouchers(Client client);

    boolean isVoucherRedeemed(Client client, String voucherNumber);
}
