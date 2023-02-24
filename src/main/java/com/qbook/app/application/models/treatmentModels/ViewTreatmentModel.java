package com.qbook.app.application.models.treatmentModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ViewTreatmentModel {
    private String treatmentId;
    private boolean isActive;
    private boolean isDoneByJunior;
    private boolean isDoneBySenior;
    private double specialPrice;
    private double juniorPrice;
    private double seniorPrice;
    private boolean special;
    private String specialEndDate;
    private int duration;
    private String treatmentDescription;
    private String treatmentName;
    private String employeeType;
}
