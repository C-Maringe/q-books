package com.qbook.app.application.models;

import lombok.Data;

@Data
public class ViewGoalModel {
    private String goalName;
    private String goalStartDate;
    private String goalMeasureDate;
    private double revenueGoalBestCase;
    private double revenueGoalWorstCase;
}
