package com.n3lx.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A POJO representing an error message that client sees on their end in JSON format.
 */
public class ApiException {

    private String message;
    private String timestamp;

    public ApiException(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

}
