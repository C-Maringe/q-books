package com.qbook.app.application.configuration.exception;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BusinessException extends RuntimeException {
    private static final long serialVersionUID = -7106081792082501074L;
    @Setter(AccessLevel.PRIVATE)
    private boolean userError;
    private final String title;

    public BusinessException(final String message, final String title, final boolean userError) {
        super(message);
        this.userError = userError;
        this.title = title;
    }

    protected BusinessException(final String message, final String title, final Throwable cause, final boolean userError) {
        super(message, cause);
        this.userError = userError;
        this.title = title;
    }
}
