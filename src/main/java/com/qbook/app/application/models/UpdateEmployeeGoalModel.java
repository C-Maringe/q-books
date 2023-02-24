package com.qbook.app.application.models;

import lombok.Data;

@Data
public class UpdateEmployeeGoalModel {
    private String employeeId;
    private String goalId;
    private String goalName;
    private String goalStartDate;
    private String goalMeasureDate;
    private double revenueGoalBestCase;
    private double revenueGoalWorstCase;
}
