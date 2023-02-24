package com.qbook.app.application.models.treatmentModels;

import lombok.Data;

@Data
public class NewTreatmentModel {
    private boolean isDoneByJunior;
    private boolean isDoneBySenior;
    private double juniorPrice;
    private double seniorPrice;
    private boolean special;
    private String specialEndDate;
    private double specialPrice;
    private int duration;
    private String treatmentDescription;
    private String treatmentName;
    private String employeeType;
}
