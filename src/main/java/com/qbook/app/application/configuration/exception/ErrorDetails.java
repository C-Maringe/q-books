package com.qbook.app.application.configuration.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
    private String title;
    private int status;
    private String detail;
    private long timeStamp;
    private String developerMessage;
}
