package com.qbook.app.application.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APNConversionResultsModel {
    private String registration_token;//NOPMD
    private String apns_token;//NOPMD
    private String status;//NOPMD
}
