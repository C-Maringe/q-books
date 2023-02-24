package com.qbook.app.application.configuration.exception.bookingsExceptions;

import com.qbook.app.application.configuration.exception.BusinessException;
import org.jetbrains.annotations.NonNls;

public class BookingNoticePeriodException extends BusinessException {
    public BookingNoticePeriodException(@NonNls String message) {
        super(message, "Booking Error", true);
    }
}
