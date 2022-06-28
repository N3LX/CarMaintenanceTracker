package com.n3lx.dao;

import com.n3lx.jdbc.JdbcConnectionFactory;
import com.n3lx.exception.CustomSQLException;
import com.n3lx.model.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VehicleDao implements GenericDao<Vehicle> {

    @Autowired
    JdbcConnectionFactory jdbcConnectionFactory;

    @Override
    public Vehicle get(int id) throws CustomSQLException {
        String query = "SELECT * FROM vehicles WHERE id=" + id + ";";
        Vehicle vehicle = new Vehicle();

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Execute query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            //Parse output
            if (resultSet.next()) {
                vehicle.setId(resultSet.getInt("id"));
                vehicle.setCustomName(resultSet.getString("custom_name"));
                vehicle.setOwnerId(resultSet.getInt("owner_id"));
                vehicle.setMake(resultSet.getString("make"));
                vehicle.setModel(resultSet.getString("model"));
            }
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
        return vehicle;
    }

    @Override
    public List<Vehicle> getAll() throws CustomSQLException {
        String query = "SELECT * FROM vehicles;";
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Execute query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            //Parse output
            while (resultSet.next()) {
                Vehicle vehicle = new Vehicle();

                vehicle.setId(resultSet.getInt("id"));
                vehicle.setCustomName(resultSet.getString("custom_name"));
                vehicle.setOwnerId(resultSet.getInt("owner_id"));
                vehicle.setMake(resultSet.getString("make"));
                vehicle.setModel(resultSet.getString("model"));

                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
        return vehicles;
    }

    @Override
    public void save(Vehicle vehicle) throws CustomSQLException {
        String query = "INSERT INTO vehicles (custom_name,owner_id,make,model) VALUES (?,?,?,?);";

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Prepare query and execute it
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, vehicle.getCustomName());
            preparedStatement.setInt(2, vehicle.getOwnerId());
            preparedStatement.setString(3, vehicle.getMake());
            preparedStatement.setString(4, vehicle.getModel());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
    }

    @Override
    public void update(Vehicle vehicle) throws CustomSQLException {
        String query = "UPDATE vehicles SET custom_name=?, owner_id=?, make=?, model=? WHERE id=?;";

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Prepare query and execute it
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, vehicle.getCustomName());
            preparedStatement.setInt(2, vehicle.getOwnerId());
            preparedStatement.setString(3, vehicle.getMake());
            preparedStatement.setString(4, vehicle.getModel());
            preparedStatement.setInt(5, vehicle.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
    }

    @Override
    public void delete(Vehicle vehicle) throws CustomSQLException {
        String query = "DELETE FROM vehicles WHERE id=? AND custom_name=? AND owner_id=? AND make=? AND model=?;";

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Prepare query and execute it
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, vehicle.getId());
            preparedStatement.setString(2, vehicle.getCustomName());
            preparedStatement.setInt(3, vehicle.getOwnerId());
            preparedStatement.setString(4, vehicle.getMake());
            preparedStatement.setString(5, vehicle.getModel());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
    }
    
}
