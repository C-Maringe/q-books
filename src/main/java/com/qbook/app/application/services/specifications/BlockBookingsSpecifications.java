package com.qbook.app.application.services.specifications;

import com.qbook.app.application.models.scheduleModels.ScheduleNewBlockoutTimeForWorkingDayModel;
import com.qbook.app.application.models.scheduleModels.ScheduleNewBlockoutTimeModel;
import com.qbook.app.domain.models.Booking;

import java.util.List;

public interface BlockBookingsSpecifications {
    List<Booking> validateBookingsAndCreate(ScheduleNewBlockoutTimeModel scheduleNewBlockoutTimeModel);

    List<Booking> validateBookingsAndCreate(ScheduleNewBlockoutTimeForWorkingDayModel scheduleNewBlockoutTimeForWorkingDayModel);
}
