package com.qbook.app.application.services.appservices;


import com.qbook.app.application.models.BookingCreatedModel;
import com.qbook.app.application.models.BookingItemModel;
import com.qbook.app.application.models.NewBookingModel;
import com.qbook.app.application.models.TimeSlotModel;
import com.qbook.app.application.models.employeeModels.EmployeeScheduleModel;
import com.qbook.app.application.models.scheduleModels.ScheduleClientModel;
import com.qbook.app.application.models.scheduleModels.ScheduleNewBookingModel;
import com.qbook.app.application.models.scheduleModels.ScheduleTreatmentModel;

import java.util.List;

public interface ScheduleServices {

	List<TimeSlotModel> viewAllTimeSlotsForEmployeeOnDate(String employeeId, String date);

	List<TimeSlotModel> viewAllTimeSlotsForEmployeeOnDateForWeb(String employeeId, String date);

	List<TimeSlotModel> viewAllTimeSlots(String date);

	List<TimeSlotModel> viewAllTimeSlotsForBlockout(String date);

	List<EmployeeScheduleModel> getAllActiveEmployeesForSchedule();

	List<BookingItemModel> viewAllBookingItemsPerEmployee(String employeeId);

	BookingCreatedModel createBookingForClient(String clientId, NewBookingModel newBookingModel);

	BookingCreatedModel createBookingForClientByEmployee(ScheduleNewBookingModel scheduleNewBookingModel);

	List<ScheduleClientModel> viewClientListForSchedule();

	List<ScheduleTreatmentModel> viewTreatmentListForScheduleAndEmployeeType(String employeeType);

	List<ScheduleTreatmentModel> viewTreatmentListForScheduleAndEmployeeTypeAndDate(String employeeType, String startDate);

	List<ScheduleTreatmentModel> viewTreatmentListForSchedule();
}
