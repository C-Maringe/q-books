package com.qbook.app.application.models;

import lombok.Data;

@Data
public class GoalModel {
    private String goalName;
    private String measureDate;
    private String revenueGoal;
    private String revenueActual;
    private String revenueDifference;
}
