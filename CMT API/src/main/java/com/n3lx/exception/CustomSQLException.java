package com.n3lx.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class CustomSQLException extends SQLException {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSQLException.class);

    public CustomSQLException(String message, String SQLState) {
        super(message, SQLState);
    }

    private String getHumanReadableMessage() {
        switch (this.getSQLState()) {
            case "08001":
                return "Could not establish the connection to the database, check if your internet connection is up.";
            default:
                LOGGER.debug("SQLException with a SQLState that is not yet translated has been thrown.\n" +
                        "SQLState: " + this.getSQLState() + "\n" +
                        "SQLException message: " + super.getMessage());
                return super.getMessage();
        }
    }

    @Override
    public String getMessage() {
        return getHumanReadableMessage();
    }

}
