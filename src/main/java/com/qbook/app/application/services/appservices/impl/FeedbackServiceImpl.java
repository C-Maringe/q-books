//package com.qbook.app.application.services.appservices.impl;
//
//import com.google.gson.JsonObject;
//import com.qbook.app.domain.models.Client;
//import com.qbook.app.domain.models.ClientFeedback;
//import com.qbook.app.application.services.appservices.FeedbackService;
//import com.qbook.app.domain.crud.ClientCrudService;
//import com.qbook.app.domain.crud.ClientFeedbackCrudService;
//import com.qbook.app.utilities.Constants;
//import com.qbook.app.utilities.ResponseResultEnum;
//import lombok.AllArgsConstructor;
//import lombok.extern.java.Log;
//import org.bson.types.ObjectId;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Log
//@Service
//@AllArgsConstructor
//public class FeedbackServiceImpl implements FeedbackService {
//
//    private final ClientCrudService clientCrudService;
//    private final ClientFeedbackCrudService clientFeedbackCrudService;
//
//    @Override
//    public JsonObject clientProvideFeedback(JsonObject clientFeedback) {
//        //find the client by id
//        if(!ObjectId.isValid(clientFeedback.get("userId").getAsString())){
//            // TODO: Fix this
////            return Json.createObjectBuilder().add(ResponseResultEnum.isSuccessful.getKey(), false).add("Message", Constants.CLIENT_FEEDBACK_FAIL_NO_CLIENT_FOUND).build();
//            return new JsonObject();
//        }
//
//        Client client = clientCrudService.getEntityBy(clientFeedback.get("userId").getAsString());
//
//        if(client != null){
//            //construct the feedback object and persist
//            ClientFeedback newClientFeedback = new ClientFeedback();
//            newClientFeedback.setClient(client);
//            newClientFeedback.setFeedbackMessage(clientFeedback.get("clientFeedbackMessage").getAsString());
//            newClientFeedback.setRating(clientFeedback.get("rating").getAsInt());
//
//            clientFeedbackCrudService.createEntity(newClientFeedback);
//
//            //update the client stating they have provided feedback
//            client.setHasGivenFeedback(true);
//            clientCrudService.updateEntity(client);
//// TODO: Fix this
////            return Json.createObjectBuilder().add(ResponseResultEnum.isSuccessful.getKey(), true).add("Message", Constants.CLIENT_FEEDBACK_SUCCESS).build();
//            return new JsonObject();
//        } else {
//            //notify the user does not exist
////            return Json.createObjectBuilder().add(ResponseResultEnum.isSuccessful.getKey(), false).add("Message", Constants.CLIENT_FEEDBACK_FAIL_NO_CLIENT_FOUND).build();
//            return new JsonObject();
//        }
//    }
//
//    @Override
//    public JsonObject adminViewProvidedFeedback() {
//        List<ClientFeedback> listOfClientsFeedback = clientFeedbackCrudService.getAllEntity();
//// TODO: Fix this
////        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
////
////        listOfClientsFeedback
////                .forEach(clientFeedback -> {
////                    jsonArrayBuilder.add(
////                            Json.createObjectBuilder()
////                                    .add("clientName",clientFeedback.getClient().getFirstName() + " " + clientFeedback.getClient().getLastName())
////                                    .add("clientMessage",clientFeedback.getFeedbackMessage())
////                                    .add("clientRating",clientFeedback.getRating())
////                                    .build()
////                    );
////                });
//
////        return Json.createObjectBuilder()
////                .add("success", true)
////                .add("feedbackList",jsonArrayBuilder.build())
////                .build();
//
//        return new JsonObject();
//    }
//
//    @Override
//    public JsonObject clientsViewProvidedFeedback() {
//        List<ClientFeedback> listOfClientsFeedback = clientFeedbackCrudService.getAllEntity();
//// TODO: Fix this
//        //transform map to Json
////        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
////
////        listOfClientsFeedback
////                .forEach(clientFeedback -> {
////                    jsonArrayBuilder.add(
////                            Json.createObjectBuilder()
////                                    .add("clientName",clientFeedback.getClient().getFirstName() + " " + clientFeedback.getClient().getLastName())
////                                    .add("clientMessage",clientFeedback.getFeedbackMessage())
////                                    .build()
////                    );
////                });
////
////        return Json.createObjectBuilder()
////                .add("success", true)
////                .add("feedbackList",jsonArrayBuilder.build())
////                .build();
//        return new JsonObject();
//    }
//}
