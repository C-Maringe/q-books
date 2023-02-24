package com.qbook.app.application.services.appservices;

import com.google.gson.JsonObject;
import com.qbook.app.application.models.analyticsModels.TopBookedClientModel;
import com.qbook.app.application.models.analyticsModels.TopBookedServiceItemModel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface AnalyticsServices {
	LinkedHashMap<String,AtomicInteger> getAllNewSignupsPM();

	LinkedHashMap<String, AtomicInteger> getAllTimeWorkedPM();

	Long getAllBookingsBefore();

	Long getAllBookingsAfter();

	Long getAllBookingsToday();

	Integer getTotalBookingsToDate();

	Integer getTotalClientsToDate();

	Integer getTotalWorkDoneToDate();

	List<TopBookedServiceItemModel> topBookedServiceItem();

	List<TopBookedClientModel> topBookedClient();
}
