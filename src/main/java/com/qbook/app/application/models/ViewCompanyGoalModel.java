package com.qbook.app.application.models;

import lombok.Data;

@Data
public class ViewCompanyGoalModel {
    private String goalName;
    private String goalStartDate;
    private String goalMeasureDate;
    private double revenueGoalBestCase;
    private double revenueGoalWorstCase;
}
