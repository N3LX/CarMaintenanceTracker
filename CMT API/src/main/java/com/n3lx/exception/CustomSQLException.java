package com.n3lx.exception;

import java.sql.SQLException;

public class CustomSQLException extends SQLException {

    public CustomSQLException(String message, String SQLState) {
        super(message, SQLState);
    }

    public String getHumanReadableMessage() {
        switch (this.getSQLState()) {
            case "08001":
                return "Could not establish the connection to the database, check if your internet connection is up.";
            default:
                return this.getMessage();
        }
    }

}
