package com.qbook.app.application.configuration.exception;

public class FirebaseSendNotificationException extends BusinessException {

    private static final long serialVersionUID = -4076277546640013396L;

    public FirebaseSendNotificationException(final String message, final String title, final Exception exception) {
        super(message,title,exception, false);
    }
}
