package com.qbook.app.application.models;

import lombok.Data;

@Data
public class EmployeeGoalProgress {
    private String employeeEmail;
    private String fullName;
    private String goalProgress;
    private String goal;
    private String measureDate;
    private String daysToMeasureDate;
}
