package com.qbook.app.application.models.analyticsModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopBookedServiceItemModel {
    private String treatmentName;
    private int count;
}
