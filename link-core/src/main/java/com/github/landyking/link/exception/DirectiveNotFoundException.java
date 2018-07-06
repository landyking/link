package com.github.landyking.link.exception;

/**
 * Created by landy on 2018/7/6.
 */
public class DirectiveNotFoundException extends LinkException {
    public DirectiveNotFoundException() {
    }

    public DirectiveNotFoundException(String message) {
        super(message);
    }

    public DirectiveNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirectiveNotFoundException(Throwable cause) {
        super(cause);
    }

    public DirectiveNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
