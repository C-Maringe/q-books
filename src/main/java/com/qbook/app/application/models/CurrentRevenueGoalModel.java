package com.qbook.app.application.models;

import lombok.Data;

@Data
public class CurrentRevenueGoalModel {
    private String employeeName;
    private String goalName;
    private String goalId;
    private String measureDate;
    private String revenueGoal;
    private int revenueGoalProgress;
}
