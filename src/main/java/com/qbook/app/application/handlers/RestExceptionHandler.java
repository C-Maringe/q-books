package com.qbook.app.application.handlers;

import com.qbook.app.application.configuration.exception.BusinessException;
import com.qbook.app.application.configuration.exception.ErrorDetails;
import com.qbook.app.application.configuration.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;

import java.nio.file.AccessDeniedException;
import java.util.Date;

/**
 * Purpose of this class is to catch global rest exceptions and ensure they are all formatted the correct way
 */
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(final ResourceNotFoundException rnfe) {
        final ErrorDetails errorDetail = new ErrorDetails();
        errorDetail.setTimeStamp(new Date().getTime());
        errorDetail.setStatus(HttpStatus.NOT_FOUND.value());
        errorDetail.setTitle(rnfe.getTitle());
        errorDetail.setDetail(rnfe.getMessage());
        return new ResponseEntity<>(errorDetail, null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDetails> handleException(final BusinessException exception) {
        final HttpStatus status = exception.isUserError() ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;
        final ErrorDetails errorDetail = new ErrorDetails();
        errorDetail.setTimeStamp(new Date().getTime());
        errorDetail.setTitle(exception.getTitle());
        errorDetail.setStatus(status.value());
        errorDetail.setDetail(exception.getMessage());
        errorDetail.setDeveloperMessage(exception.getClass().getName());
        return new ResponseEntity<>(errorDetail, null, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleException(final NativeWebRequest request, final Exception exception) {
        final ErrorDetails errorDetail = new ErrorDetails();
        errorDetail.setDetail("A general error has occurred. Please contact an administrator.");
        errorDetail.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetail.setTimeStamp(new Date().getTime());
        errorDetail.setTitle("General Error");
        return new ResponseEntity<>(errorDetail, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(final NativeWebRequest request) {
        final ErrorDetails errorDetail = new ErrorDetails();
        errorDetail.setDetail("You do not have rights to access this resource.");
        errorDetail.setStatus(HttpStatus.FORBIDDEN.value());
        errorDetail.setTimeStamp(new Date().getTime());
        errorDetail.setTitle("Unauthorized Access");
        return new ResponseEntity<>(errorDetail, null, HttpStatus.FORBIDDEN);
    }
}
