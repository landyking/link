package com.github.landyking.link.exception;

/**
 * Created by landy on 2018/8/17.
 */
public class ExecutionAssertException extends LinkException {
    public ExecutionAssertException() {
    }

    public ExecutionAssertException(String message) {
        super(message);
    }

    public ExecutionAssertException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecutionAssertException(Throwable cause) {
        super(cause);
    }

    public ExecutionAssertException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
