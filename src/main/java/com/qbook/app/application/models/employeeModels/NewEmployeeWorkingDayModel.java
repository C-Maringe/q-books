package com.qbook.app.application.models.employeeModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEmployeeWorkingDayModel {
    @NonNull
    private String employeeWorkingId;
    @NonNull
    private long employeeWorkingDayStartTime;
    @NonNull
    private long employeeWorkingDayEndTime;
    @NonNull
    private long employeeWorkingDayLunchStartTime;
    @NonNull
    private int employeeWorkingDayLunchDuration;
    @NonNull
    private boolean applyToWholeWeek;
}
