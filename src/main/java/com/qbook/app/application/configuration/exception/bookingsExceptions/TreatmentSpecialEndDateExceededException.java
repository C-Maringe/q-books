package com.qbook.app.application.configuration.exception.bookingsExceptions;

import com.qbook.app.application.configuration.exception.BusinessException;
import org.jetbrains.annotations.NonNls;

public class TreatmentSpecialEndDateExceededException extends BusinessException {
    public TreatmentSpecialEndDateExceededException(@NonNls String message) {
        super(message, "Booking Error", true);
    }
}
