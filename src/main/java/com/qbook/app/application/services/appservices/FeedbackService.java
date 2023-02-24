package com.qbook.app.application.services.appservices;

import com.google.gson.JsonObject;

/**
 * Created by ironhulk on 2017/10/19.
 */
public interface FeedbackService {
    /**
     * @param clientFeedback Json Object containing the client Feedback
     * @return the JsonObject with the result of persisting the client feedback
     *
     */
    JsonObject clientProvideFeedback(JsonObject clientFeedback);

    /**
     * @return the JsonObject with the collection of client feedback provided till current date
     *
     */
    JsonObject adminViewProvidedFeedback();

    /**
     * @return the JsonObject with the collection of client feedback provided till current date for clients to view
     *
     */
    JsonObject clientsViewProvidedFeedback();
}
