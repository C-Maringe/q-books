package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.models.analyticsModels.TopBookedClientModel;
import com.qbook.app.application.models.analyticsModels.TopBookedServiceItemModel;
import com.qbook.app.application.services.appservices.AnalyticsServices;
import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.BookingList;
import com.qbook.app.domain.models.BookingListItem;
import com.qbook.app.domain.models.Client;
import com.qbook.app.domain.repository.BookingRepository;
import com.qbook.app.domain.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
public class AnalyticsServicesImpl implements AnalyticsServices {
	private final BookingRepository bookingRepository;
	private final ClientRepository clientRepository;

	@Override
	public LinkedHashMap<String, AtomicInteger> getAllNewSignupsPM() {
		DateTime firstDayAndWeekOfCurrentYear = DateTime.now().withWeekOfWeekyear(1).withDayOfWeek(1).withTimeAtStartOfDay();
		List<Client> allClientsPerYear = clientRepository.findAllByDateRegisteredAfter(firstDayAndWeekOfCurrentYear.getMillis());

		LinkedHashMap<String, AtomicInteger> hashMap = initialiseMap();

		allClientsPerYear
				.forEach(client -> {
					int monthOfRegistration = new DateTime(client.getDateRegistered()).getMonthOfYear();
					hashMap.get(getMonthName(monthOfRegistration)).getAndAdd(1);
				});
		return hashMap;
	}

	@Override
	public LinkedHashMap<String, AtomicInteger> getAllTimeWorkedPM() {
		// get all bookings for the current year
		DateTime firstDayAndWeekOfCurrentYear = DateTime.now().withWeekOfWeekyear(1).withDayOfWeek(1).withTimeAtStartOfDay();
		List<Booking> bookingsPerMonthForTheYear = bookingRepository.findAllByBookingStatusAndDayToBlockOutAndStartDateTimeAfter("Active", false, firstDayAndWeekOfCurrentYear.getMillis());

		// segment the bookings into months
		LinkedHashMap<String, AtomicInteger> arrayOfMonthTotals = initialiseMap();

		// for each month calculate the total bookings  duration
		bookingsPerMonthForTheYear
				.forEach(booking -> {
					int monthOfBooking = new DateTime(booking.getStartDateTime()).getMonthOfYear();
					arrayOfMonthTotals.get(getMonthName(monthOfBooking)).addAndGet(1);
				});

		return arrayOfMonthTotals;
	}

	@Override
	public Long getAllBookingsBefore() {
		return (long) bookingRepository
				.findAllByBookingStatusAndDayToBlockOutAndStartDateTimeBefore("Active", false, DateTime.now().getMillis())
				.size();
	}

	@Override
	public Long getAllBookingsAfter() {
		return bookingRepository
				.countByBookingStatusAndDayToBlockOutAndStartDateTimeAfter("Active", false, DateTime.now().getMillis());
	}

	@Override
	public Long getAllBookingsToday() {
		return bookingRepository
				.countByBookingStatusAndDayToBlockOutAndStartDateTimeAfterAndEndDateTimeBefore
						("Active", false, DateTime.now().withTimeAtStartOfDay().getMillis(), DateTime.now().plusDays(1).withTimeAtStartOfDay().getMillis());
	}

	@Override
	public Integer getTotalBookingsToDate() {
		return bookingRepository
				.findAllByBookingStatusAndDayToBlockOut("Active", false).size();
	}

	@Override
	public Integer getTotalClientsToDate() {
		return (int)clientRepository.countByIsActive(true);
	}

	@Override
	public Integer getTotalWorkDoneToDate() {
		return bookingRepository
				.findAllByBookingStatusAndDayToBlockOut("Active", false)
				.parallelStream()
				.map(Booking::getDuration)
				.reduce(0, Integer::sum);
	}

	@Override
	public List<TopBookedServiceItemModel> topBookedServiceItem() {
		List<Booking> listOfBookings = bookingRepository
				.findAllByBookingStatusAndDayToBlockOut("Active", false);
		HashMap<String, AtomicInteger> listOfTopTreatment = new HashMap<>();

		for (Booking booking : listOfBookings) {
			BookingList bookingList = booking.getBookingList();
			for (BookingListItem bookingListItem :bookingList.getBookingListItems()) {
				if (bookingListItem.getTreatmentQuantity() > 0) {
					if (listOfTopTreatment.containsKey(bookingListItem.getTreatment().getTreatmentName())) {
						listOfTopTreatment.get(bookingListItem.getTreatment().getTreatmentName()).getAndAdd(1);
					} else {
						//new value so just initialise count
						listOfTopTreatment.put(bookingListItem.getTreatment().getTreatmentName(), new AtomicInteger(1));
					}
				}
			}
		}
		HashMap<String, Integer> listOfTopTreatmentConverted = new HashMap<>();

		listOfTopTreatment
				.forEach((treatmentName, total) ->
						listOfTopTreatmentConverted.put(treatmentName, total.intValue())
				);

		List<TopBookedServiceItemModel> topBookedServiceItemModels = new ArrayList<>();
		listOfTopTreatmentConverted
				.entrySet().stream()
				.sorted(Map.Entry.comparingByValue())
				.forEach((entry) -> topBookedServiceItemModels.add(
						TopBookedServiceItemModel
						.builder()
						.treatmentName(entry.getKey())
						.count(entry.getValue())
						.build()
				));

		return topBookedServiceItemModels;
	}

	@Override
	public List<TopBookedClientModel> topBookedClient() {
		//get all bookings that were successful
		List<Booking> allSuccessfulBookings = bookingRepository
				.findAllByBookingStatusAndDayToBlockOut("Active", false);

		HashMap<String, AtomicInteger> listOfTopClients = new HashMap<>();

		for(Booking booking : allSuccessfulBookings){
			//iterate over the topClientList to see if the user is already in the list
			if (listOfTopClients.containsKey(booking.getClient().getFirstName() + " " + booking.getClient().getLastName() + "(" + booking.getClient().getUsername() + ")"))
				listOfTopClients.get(booking.getClient().getFirstName() + " " + booking.getClient().getLastName() + "(" + booking.getClient().getUsername() + ")").getAndAdd(1);
			else
				listOfTopClients.put(booking.getClient().getFirstName() + " " + booking.getClient().getLastName() + "(" + booking.getClient().getUsername() + ")", new AtomicInteger(1));
		}

		HashMap<String, Integer> listOfTopClientConverted = new HashMap<>();

		listOfTopClients
				.forEach((treatmentName, total) ->
						listOfTopClientConverted.put(treatmentName, total.intValue())
				);

		List<TopBookedClientModel> topBookedClientModels = new ArrayList<>();
		listOfTopClientConverted
				.entrySet().stream()
				.sorted(Map.Entry.comparingByValue())
				.forEach((entry) -> topBookedClientModels.add(
						TopBookedClientModel
						.builder()
						.clientName(entry.getKey())
						.count(entry.getValue())
						.build()
				));
		return topBookedClientModels;
	}

	private LinkedHashMap<String,AtomicInteger> initialiseMap(){
		LinkedHashMap<String, AtomicInteger> hashMap = new LinkedHashMap<>();
		for(int i = 1;i < 13;i++){
			hashMap.put(getMonthName(i),new AtomicInteger());
		}
		return hashMap;
	}

	private String getMonthName(int month) {
		switch (month) {
			case 1:
				return "January";
			case 2:
				return "February";
			case 3:
				return "March";
			case 4:
				return "April";
			case 5:
				return "May";
			case 6:
				return "June";
			case 7:
				return "July";
			case 8:
				return "August";
			case 9:
				return "September";
			case 10:
				return "October";
			case 11:
				return "November";
			case 12:
				return "December";
		}

		return "";
	}
}
