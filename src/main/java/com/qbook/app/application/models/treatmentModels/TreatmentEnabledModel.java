package com.qbook.app.application.models.treatmentModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TreatmentEnabledModel {
    private boolean success;
    private String message;
}
