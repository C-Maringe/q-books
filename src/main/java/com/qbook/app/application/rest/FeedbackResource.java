//package com.qbook.app.application.rest;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import com.qbook.app.application.services.appservices.FeedbackService;
//import lombok.AllArgsConstructor;
//import lombok.extern.java.Log;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Log
//@RestController
//@RequestMapping("/api/feedback")
//@AllArgsConstructor
//public class FeedbackResource {
//
//    private final FeedbackService feedbackService;
//    private final Gson gson;
//    @PostMapping
//    public ResponseEntity<JsonObject> clientLogFeedback(String clientFeedback) {
//        JsonObject output = feedbackService.clientProvideFeedback(gson.fromJson(clientFeedback, JsonObject.class));
//
//        return new ResponseEntity<>(output, HttpStatus.CREATED);
//    }
//
//    @GetMapping
//    public ResponseEntity<JsonObject> clientViewFeedback() {
//        return new ResponseEntity<>(feedbackService.clientsViewProvidedFeedback(), HttpStatus.OK);
//    }
//
//    @GetMapping("Admin")
//    public ResponseEntity<JsonObject> adminViewFeedback() {
//        return new ResponseEntity<>(feedbackService.adminViewProvidedFeedback(), HttpStatus.OK);
//    }
//}
