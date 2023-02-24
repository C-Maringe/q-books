package com.qbook.app.application.models.analyticsModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopBookedClientModel {
    private String clientName;
    private int count;
}
