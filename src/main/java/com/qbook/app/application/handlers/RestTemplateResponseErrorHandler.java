package com.qbook.app.application.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qbook.app.application.configuration.exception.ErrorDetails;
import com.qbook.app.application.configuration.exception.ResourceClientErrorHandlerException;
import com.qbook.app.application.configuration.exception.ResourceClientResponseException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.util.Scanner;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Log
@Component
@AllArgsConstructor
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(final ClientHttpResponse httpResponse) {
        final boolean hasError;
        try {
            hasError = httpResponse.getStatusCode().series() == CLIENT_ERROR
                    || httpResponse.getStatusCode().series() == SERVER_ERROR;
        } catch (IOException e) {
            final String exceptionMessage = "An error occurred while handling resource client error response.";
            log.severe(exceptionMessage);
            throw new ResourceClientErrorHandlerException(exceptionMessage);
        }
        return hasError;
    }

    @Override
    public void handleError(final ClientHttpResponse response) {
        try {
            Scanner s = new Scanner(response.getBody()).useDelimiter("\\A");
            System.out.println(s.hasNext() ? s.next() : "");
            ErrorDetails error = objectMapper.readValue(response.getBody(), ErrorDetails.class);
            throw new ResourceClientResponseException(error.getDeveloperMessage());
        } catch (IOException e) {
            final String exceptionMessage = "An error occurred while handling resource client error response.";
            log.severe(exceptionMessage);
            throw new ResourceClientErrorHandlerException(exceptionMessage);
        }
    }
}
