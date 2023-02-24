package com.qbook.app.application.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingCancellationNotificationMessage {
   private String email;
   private String fullName;
   private String startDateTime;
   private String employeeAvailable;
}
