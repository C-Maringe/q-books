package com.qbook.app.application.configuration.exception.blockOutExceptions;

import com.qbook.app.application.configuration.exception.BusinessException;
import lombok.NonNull;

public class BlockoutDayFailedException extends BusinessException {

    public BlockoutDayFailedException(@NonNull String message){
        super(message, "Schedule Error", true);
    }
}
