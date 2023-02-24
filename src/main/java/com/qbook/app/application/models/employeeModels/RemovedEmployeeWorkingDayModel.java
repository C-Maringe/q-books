package com.qbook.app.application.models.employeeModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemovedEmployeeWorkingDayModel {
    private String message;
    private boolean success;
}
