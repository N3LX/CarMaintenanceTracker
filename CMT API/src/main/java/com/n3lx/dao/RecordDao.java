package com.n3lx.dao;

import com.n3lx.jdbc.JdbcConnectionFactory;
import com.n3lx.exception.CustomSQLException;
import com.n3lx.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RecordDao implements GenericDao<Record> {

    @Autowired
    JdbcConnectionFactory jdbcConnectionFactory;

    @Override
    public Record get(int id) throws CustomSQLException {
        String query = "SELECT * FROM records WHERE id=" + id + ";";
        Record record = new Record();

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Execute query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            //Parse output
            if (resultSet.next()) {
                record.setId(resultSet.getInt("id"));
                record.setVehicleId(resultSet.getInt("vehicle_id"));
                record.setCreationDate(resultSet.getObject("creation_date", LocalDate.class));
                record.setMileage(resultSet.getInt("mileage"));
                record.setShortDescription(resultSet.getString("short_description"));
                record.setLongDescription(resultSet.getString("long_description"));
            }
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
        return record;
    }

    @Override
    public List<Record> getAll() throws CustomSQLException {
        String query = "SELECT * FROM records;";
        List<Record> records = new ArrayList<>();

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Execute query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            //Parse output
            while (resultSet.next()) {
                Record record = new Record();

                record.setId(resultSet.getInt("id"));
                record.setVehicleId(resultSet.getInt("vehicle_id"));
                record.setCreationDate(resultSet.getObject("creation_date", LocalDate.class));
                record.setMileage(resultSet.getInt("mileage"));
                record.setShortDescription(resultSet.getString("short_description"));
                record.setLongDescription(resultSet.getString("long_description"));

                records.add(record);
            }
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
        return records;
    }

    @Override
    public void save(Record record) throws CustomSQLException {
        String query = "INSERT INTO records (vehicle_id,creation_date,mileage,short_description,long_description) " +
                "VALUES (?,?,?,?,?);";

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Prepare query and execute it
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, record.getVehicleId());
            preparedStatement.setObject(2, record.getCreationDate());
            preparedStatement.setInt(3, record.getMileage());
            preparedStatement.setString(4, record.getShortDescription());
            preparedStatement.setString(5, record.getLongDescription());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
    }

    @Override
    public void update(Record record) throws CustomSQLException {
        String query = "UPDATE records " +
                "SET vehicle_id=?, creation_date=?, mileage=?, short_description=?, long_description=? " +
                "WHERE id=?";

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Prepare query and execute it
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, record.getVehicleId());
            preparedStatement.setDate(2, Date.valueOf(record.getCreationDate()));
            preparedStatement.setInt(3, record.getMileage());
            preparedStatement.setString(4, record.getShortDescription());
            preparedStatement.setString(5, record.getLongDescription());
            preparedStatement.setInt(6, record.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
    }

    @Override
    public void delete(Record record) throws CustomSQLException {
        String query = "DELETE FROM records " +
                "WHERE id=? AND vehicle_id=? AND creation_date=? AND mileage=? " +
                "AND short_description=? AND long_description=?;";

        try (Connection connection = jdbcConnectionFactory.getConnection()) {
            //Prepare query and execute it
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, record.getId());
            preparedStatement.setInt(2, record.getVehicleId());
            preparedStatement.setObject(3, record.getCreationDate());
            preparedStatement.setInt(4, record.getMileage());
            preparedStatement.setString(5, record.getShortDescription());
            preparedStatement.setString(6, record.getLongDescription());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
    }

}
