package com.github.landyking.link.exception;

/**
 * Created by landy on 2018/7/6.
 */
public class LinkException extends Exception {
    public LinkException() {
    }

    public LinkException(String message) {
        super(message);
    }

    public LinkException(String message, Throwable cause) {
        super(message, cause);
    }

    public LinkException(Throwable cause) {
        super(cause);
    }

    public LinkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
