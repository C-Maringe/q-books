package com.qbook.app.application.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationBodyModel {
    private String to;//NOPMD
    private List<String> registration_ids;//NOPMD
    private PushNotificationMessageModel notification;
}
