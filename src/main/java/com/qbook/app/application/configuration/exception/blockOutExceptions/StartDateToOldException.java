package com.qbook.app.application.configuration.exception.blockOutExceptions;

import com.qbook.app.application.configuration.exception.BusinessException;
import org.jetbrains.annotations.NonNls;


public class StartDateToOldException extends BusinessException {

    public StartDateToOldException(@NonNls String message){
        super(message, "Schedule Error", true);
    }
}
