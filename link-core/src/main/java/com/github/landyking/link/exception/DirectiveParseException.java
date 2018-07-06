package com.github.landyking.link.exception;

/**
 * Created by landy on 2018/7/6.
 */
public class DirectiveParseException extends LinkException {
    public DirectiveParseException() {
    }

    public DirectiveParseException(String message) {
        super(message);
    }

    public DirectiveParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirectiveParseException(Throwable cause) {
        super(cause);
    }

    public DirectiveParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
