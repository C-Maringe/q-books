package com.qbook.app.domain.models;

import lombok.Data;

@Data
public class EmployeeWorkingDay {
    private String employeeWorkingDayId;
    private String employeeWorkingDayName;
    private long employeeWorkingDayStartTime;
    private long employeeWorkingDayEndTime;
    private long employeeWorkingDayLunchStartTime;
    private int employeeWorkingDayLunchDuration;
}
