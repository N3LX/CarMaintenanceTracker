package com.n3lx.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class that provides Connection objects that are already configured for use with database.
 */
@Component
public class PostgresJdbcConnectionFactory implements JdbcConnectionFactory {

    @Value("${db.url}")
    private String url;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresJdbcConnectionFactory.class);


    public PostgresJdbcConnectionFactory() {
        //Load the compatible JDBC driver
        try {
            Class.forName("org.postgresql.Driver");
            LOGGER.info("org.postgresql.Driver has been successfully loaded.");
        } catch (ClassNotFoundException e) {
            LOGGER.error("There was a problem with loading the driver, " +
                    "connection to the database might not have been established.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

}