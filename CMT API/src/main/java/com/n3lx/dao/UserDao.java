package com.n3lx.dao;

import com.n3lx.jdbc.JdbcConnectionFactory;
import com.n3lx.jdbc.exception.CustomSQLException;
import com.n3lx.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao implements GenericDao<User> {

    @Autowired
    JdbcConnectionFactory jdbcConnectionFactory;

    @Override
    public User get(int id) throws CustomSQLException {
        String query = "SELECT * FROM users WHERE id=" + id + ";";
        User user = new User();

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Execute query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            //Parse output
            if (resultSet.next()) {
                user.setId(resultSet.getInt("id"));
                user.setUserName(resultSet.getString("user_name"));
            }
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
        return user;
    }

    @Override
    public List<User> getAll() throws CustomSQLException {
        String query = "SELECT * FROM users;";
        List<User> users = new ArrayList<>();

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Execute query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            //Parse output
            while (resultSet.next()) {
                User user = new User();

                user.setId(resultSet.getInt("id"));
                user.setUserName(resultSet.getString("user_name"));

                users.add(user);
            }
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
        return users;
    }

    @Override
    public void save(User user) throws CustomSQLException {
        String query = "INSERT INTO users (user_name)" + "VALUES (?);";

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Prepare query and execute it
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, user.getUserName());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
    }

    @Override
    public void update(User user) throws CustomSQLException {
        String query = "UPDATE users SET user_name=? WHERE id=?;";

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Prepare query and execute it
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setInt(2, user.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
    }

    @Override
    public void delete(User user) throws CustomSQLException {
        String query = "DELETE FROM users WHERE id=? AND user_name=?;";

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Prepare query and execute it
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, user.getId());
            preparedStatement.setString(2, user.getUserName());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
    }

}
