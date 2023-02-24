package com.qbook.app.application.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationMessageResponseModel {//NOPMD
    private Long multicast_id;//NOPMD
    private Integer success;
    private Integer failure;
    private Long canonical_ids;//NOPMD
    private List<PushNotificationMessageResponseResultsModel> results;
}
