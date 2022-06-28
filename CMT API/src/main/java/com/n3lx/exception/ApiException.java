package com.n3lx.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A POJO representing an error message that client sees on their end in JSON format.
 */
public class ApiException {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiException.class);

    private String message;
    private String timestamp;

    public ApiException(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        //Log this incident
        LOGGER.debug("An API exception has been thrown with following details:\n" +
                "Message: " + message);
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

}
