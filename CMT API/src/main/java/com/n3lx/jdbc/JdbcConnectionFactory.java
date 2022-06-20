package com.n3lx.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface JdbcConnectionFactory {

    Connection getConnection() throws SQLException;

}
