package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.Client;
import com.qbook.app.domain.models.Employee;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, ObjectId> {

    List<Booking> findAllByWorkingDayId(String workingDayId);
    List<Booking> findAllByBookingStatus(String bookingStatus);
    List<Booking> findAllByEmployeeAndBookingStatus(Employee employee, String bookingStatus);
    List<Booking> findAllByEmployeeAndBookingStatusAndStartDateTimeBetween(Employee employee, String bookingStatus, Long startDateTime, Long endDateTime);
    List<Booking> findAllByEndDateTimeGreaterThanAndStartDateTimeLessThanAndBookingStatus(Long endDateTime, Long startDateTime, String bookingStatus);
    List<Booking> findAllByEndDateTimeLessThanEqualAndStartDateTimeGreaterThanEqualAndBookingStatus(Long endDateTime, Long startDateTime, String bookingStatus);
    List<Booking> findAllByEndDateTimeGreaterThanEqualAndStartDateTimeLessThanEqualAndBookingStatus(Long endDateTime, Long startDateTime, String bookingStatus);
    List<Booking> findAllByEndDateTime(Long endDateTime);
    List<Booking> findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndBookingStatusAndDayToBlockOut(Long startDateTime, Long endDateTime, String bookingStatus, boolean dayToBlockOut);
    List<Booking> findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndDayToBlockOut(Long startDateTime, Long endDateTime, boolean dayToBlockOut);
    List<Booking> findAllByBookingStatusAndDayToBlockOutAndClient(String bookingStatus, boolean dayToBlockOut, Client client);
    List<Booking> findAllByEndDateTimeLessThanEqualAndStartDateTimeGreaterThanEqualAndBookingStatusAndEmployee(Long endDateTime, Long startDateTime, String bookingStatus, Employee employee);
    List<Booking> findAllByBookingStatusAndDayToBlockOutAndStartDateTimeAfter(String bookingStatus, boolean dayToBlockOut, Long startDateTime);
    long countByBookingStatusAndDayToBlockOutAndStartDateTimeAfter(String bookingStatus, boolean dayToBlockOut, Long startDateTime);
    long countByBookingStatusAndDayToBlockOutAndStartDateTimeAfterAndEndDateTimeBefore(String bookingStatus, boolean dayToBlockOut, Long startDateTime, Long endDateTime);
    List<Booking> findAllByBookingStatusAndDayToBlockOutAndStartDateTimeBefore(String bookingStatus, boolean dayToBlockOut, Long startDateTime);
    List<Booking> findAllByBookingStatusAndDayToBlockOut(String bookingStatus, boolean dayToBlockOut);
    List<Booking> findAllByBookingStatusAndDayToBlockOutAndClientOrderByStartDateTimeDesc(String bookingStatus, boolean dayToBlockOut, Client client);
    List<Booking> findAllByBookingStatusAndDayToBlockOutAndClientOrderByDateCreatedDesc(String bookingStatus, boolean dayToBlockOut, Client client);
    List<Booking> findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndBookingStatusAndDayToBlockOutAndClientOrderByStartDateTimeDesc(Long startDateTime, Long endDateTime, String bookingStatus, boolean dayToBlockOut, Client client);
    List<Booking> findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndDayToBlockOutAndClientOrderByStartDateTimeDesc(Long startDateTime, Long endDateTime, boolean dayToBlockOut, Client client);
}
