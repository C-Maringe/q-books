package com.qbook.app.application.models;

import lombok.Data;

@Data
public class BookingCancellationQueueMember {
    private long startDate;
    private long endDate;
    private String startTime;
    private String endTime;
    private String clientId;
    private String employeeId;
}
  