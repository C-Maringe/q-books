package com.qbook.app.application.rest.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qbook.app.application.models.analyticsModels.TopBookedClientModel;
import com.qbook.app.application.models.analyticsModels.TopBookedServiceItemModel;
import com.qbook.app.application.services.appservices.AnalyticsServices;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

@Log
@RestController
@RequestMapping("/api/auth/analytics")
@AllArgsConstructor
public class AnalyticsResource {

    private final AnalyticsServices analyticsServices;
    
    @GetMapping("bookings")
    public ResponseEntity<String> getAllBookings() {
        log.log(Level.INFO, "AnalyticsResource.getAllBookings() called at " + System.currentTimeMillis());

        JsonObject newBookings = new JsonObject();
        newBookings.addProperty("value",analyticsServices.getAllBookingsAfter());
        newBookings.addProperty("color", "#46BFBD");
        newBookings.addProperty("highlight", "#5AD3D1");
        newBookings.addProperty("label", "Upcoming Bookings");

        JsonObject currBookings = new JsonObject();
        currBookings.addProperty("value",analyticsServices.getAllBookingsToday());
        currBookings.addProperty("color", "#FDB45C");
        currBookings.addProperty("highlight", "#FFC870");
        currBookings.addProperty("label", "Bookings Today");

        JsonArray bookingDetails = new JsonArray();
//        bookingDetails.add(oldBookings);
        bookingDetails.add(newBookings);
        bookingDetails.add(currBookings);

        log.log(Level.INFO, "AnalyticsResource.getAllBookings() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(bookingDetails.toString(), HttpStatus.OK);
    }

    @GetMapping("signups")
    public ResponseEntity<String> getAllSignups() {
        log.log(Level.INFO, "AnalyticsResource.getAllSignups() called at " + System.currentTimeMillis());

        HashMap<String, AtomicInteger> values = analyticsServices.getAllNewSignupsPM();
        JsonArray labelArray = new JsonArray();

        for (String labels : values.keySet()) {
            labelArray.add(labels);
        }

        //actual data
        JsonArray dataArray = new JsonArray();
        for (String labels:values.keySet()) {
            dataArray.add(""+values.get(labels));
        }


        JsonObject dataSet = new JsonObject();
        dataSet.addProperty("fillColor", "rgba(220,220,220,0.5)");
        dataSet.addProperty("strokeColor", "rgba(220,220,220,0.8)");
        dataSet.addProperty("highlightFill", "rgba(220,220,220,0.75)");
        dataSet.addProperty("fillColor", "rgba(220,220,220,0.5)");
        dataSet.addProperty("highlightStroke", "rgba(220,220,220,1)");
        dataSet.add("data", dataArray);

        JsonArray dataSetArray = new JsonArray();
        dataSetArray.add(dataSet);

        JsonObject chartDataSet = new JsonObject();
        chartDataSet.add("labels", labelArray);
        chartDataSet.add("datasets", dataSetArray);

        log.log(Level.INFO, "AnalyticsResource.getAllSignups() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(chartDataSet.toString(), HttpStatus.OK);
    }

    @GetMapping("timeWorked")
    public ResponseEntity<String> getTotalTimeWorked() {
        log.log(Level.INFO, "AnalyticsResource.getTotalTimeWorked() called at " + System.currentTimeMillis());
        HashMap<String, AtomicInteger> values = analyticsServices.getAllTimeWorkedPM();
        JsonArray labelArray = new JsonArray();

        for (String labels : values.keySet()) {
            labelArray.add(labels);
        }

        //actual data
        JsonArray dataArray = new JsonArray();
        for (String labels : values.keySet()) {
            dataArray.add(""+values.get(labels));
        }


        JsonObject dataSet = new JsonObject();
        dataSet.addProperty("fillColor", "rgba(220,220,220,0.2)");
        dataSet.addProperty("strokeColor", "rgba(220,220,220,1)");
        dataSet.addProperty("pointColor", "rgba(220,220,220,1)");
        dataSet.addProperty("pointStrokeColor", "#fff");
        dataSet.addProperty("pointHighlightFill", "#fff");
        dataSet.addProperty("pointHighlightStroke", "rgba(220,220,220,1)");
        dataSet.add("data", dataArray);

        JsonArray dataSetArray = new JsonArray();
        dataSetArray.add(dataSet);


        JsonObject chartDataSet = new JsonObject();
        chartDataSet.add("labels", labelArray);
        chartDataSet.add("datasets", dataSetArray);

        log.log(Level.INFO, "AnalyticsResource.getTotalTimeWorked() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(chartDataSet.toString(), HttpStatus.OK);
    }

    @GetMapping("totalClientsToDate")
    public ResponseEntity<String> totalClientsToDate() {
        log.log(Level.INFO, "AnalyticsResource.totalClientsToDate() called at " + System.currentTimeMillis());
        JsonObject dataSet = new JsonObject();
        dataSet.addProperty("totalClients", analyticsServices.getTotalClientsToDate());
        log.log(Level.INFO, "AnalyticsResource.totalClientsToDate() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(dataSet.toString(), HttpStatus.OK);
    }

    @GetMapping("totalBookingsToDate")
    public ResponseEntity<String> totalBookingsToDate() {
        log.log(Level.INFO, "AnalyticsResource.totalBookingsToDate() called at " + System.currentTimeMillis());
        JsonObject dataSet = new JsonObject();
        dataSet.addProperty("totalBookings", analyticsServices.getTotalBookingsToDate());
        log.log(Level.INFO, "AnalyticsResource.totalBookingsToDate() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(dataSet.toString(), HttpStatus.OK);
    }

    @GetMapping("totalWorkedToDate")
    public ResponseEntity<String> totalWorkedToDate() {
        log.log(Level.INFO, "AnalyticsResource.totalWorkedToDate() called at " + System.currentTimeMillis());
        JsonObject dataSet = new JsonObject();
        dataSet.addProperty("totalWorkDone", analyticsServices.getTotalWorkDoneToDate()/60);
        log.log(Level.INFO, "AnalyticsResource.totalWorkedToDate() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(dataSet.toString(), HttpStatus.OK);
    }

    @GetMapping("topBookedServiceItem")
    public ResponseEntity<List<TopBookedServiceItemModel>> getTopBookedServiceItem() {
        log.log(Level.INFO, "AnalyticsResource.getTopBookedServiceItem() called at " + System.currentTimeMillis());
        log.log(Level.INFO, "AnalyticsResource.getTopBookedServiceItem() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(analyticsServices.topBookedServiceItem(), HttpStatus.OK);
    }

    @GetMapping("topBookedClient")
    public ResponseEntity<List<TopBookedClientModel>> getTopBookedClient() {
        log.log(Level.INFO, "AnalyticsResource.getTopBookedClient() called at " + System.currentTimeMillis());
        log.log(Level.INFO, "AnalyticsResource.getTopBookedClient() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(analyticsServices.topBookedClient(), HttpStatus.OK);
    }
}
