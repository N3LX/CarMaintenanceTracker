package com.n3lx.jdbc;

import java.sql.Connection;

public interface JdbcConnectionFactory {

    Connection getConnection();

}
