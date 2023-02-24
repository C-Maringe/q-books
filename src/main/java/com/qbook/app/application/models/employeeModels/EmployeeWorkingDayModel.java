package com.qbook.app.application.models.employeeModels;

import lombok.Data;

@Data
public class EmployeeWorkingDayModel {
    private String employeeWorkingDayId;
    private String workingDayName;
    private long workingDayStartTime;
    private long workingDayEndTime;
    private long workingDayLunchStartTime;
    private int workingDayLunchDuration;
}
