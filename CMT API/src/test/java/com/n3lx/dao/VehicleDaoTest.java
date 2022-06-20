package com.n3lx.dao;

import com.n3lx.jdbc.JdbcConnectionFactory;
import com.n3lx.jdbc.exception.CustomSQLException;
import com.n3lx.model.Vehicle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:servlet-context-test.xml")
public class VehicleDaoTest {

    @Autowired
    GenericDao<Vehicle> vehicleDao;

    @Autowired
    JdbcConnectionFactory jdbcConnectionFactory;

    @Before
    public void fillDbWithData() throws SQLException, InterruptedException {
        var connection = jdbcConnectionFactory.getConnection();
        var statement = connection.createStatement();

        //Reset the sequence between each test for easier data insertion (id fields will always be the same)
        statement.executeUpdate("ALTER SEQUENCE users_id_seq RESTART");
        statement.executeUpdate("ALTER SEQUENCE vehicles_id_seq RESTART");
        statement.executeUpdate("ALTER SEQUENCE records_id_seq RESTART");

        //Insert data
        statement.executeUpdate("INSERT INTO users (user_name) VALUES ('John Doe');");
        statement.executeUpdate("INSERT INTO users (user_name) VALUES ('Jan Kowalski');");

        statement.executeUpdate("INSERT INTO vehicles (custom_name, owner_id, make, model) VALUES\n" +
                "('Golf',1,'Volkswagen','Golf V');");
        statement.executeUpdate("INSERT INTO vehicles (custom_name, owner_id, make, model) VALUES\n" +
                "('Ford',2,'Ford','Focus MK4');");

        statement.executeUpdate("INSERT INTO records (vehicle_id,creation_date,mileage,short_description,long_description) VALUES\n" +
                "(1,CURRENT_DATE, 378000,'Clutch replacement','Clutch and gearbox oil were replaced for total of 3500 PLN');");
        statement.executeUpdate("INSERT INTO records (vehicle_id,creation_date,mileage,short_description,long_description) VALUES\n" +
                "(1,CURRENT_DATE, 378500,'Oil change','Engine oil change');");

        connection.close();

        //Give time to DB to process those queries
        Thread.sleep(30);
    }

    @After
    public void deleteAllDataFromDb() throws SQLException {
        var connection = jdbcConnectionFactory.getConnection();
        var statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM records;");
        statement.executeUpdate("DELETE FROM vehicles;");
        statement.executeUpdate("DELETE FROM users;");
        connection.close();
    }

    @Test
    public void testGet() throws CustomSQLException {
        //Verify individual records
        Vehicle vehicle = vehicleDao.get(1);

        assertEquals("Golf", vehicle.getCustomName());
        assertEquals(1, vehicle.getOwnerId());
        assertEquals("Volkswagen", vehicle.getMake());
        assertEquals("Golf V", vehicle.getModel());

        Vehicle vehicle2 = vehicleDao.get(2);

        assertEquals("Ford", vehicle2.getCustomName());
        assertEquals(2, vehicle2.getOwnerId());
        assertEquals("Ford", vehicle2.getMake());
        assertEquals("Focus MK4", vehicle2.getModel());
    }

    @Test
    public void testGetAll() throws CustomSQLException {
        List<Vehicle> vehicles = vehicleDao.getAll();

        //Check list size
        assertEquals(2, vehicles.size());

        //Verify individual records from the list
        Vehicle vehicle = vehicles.get(0);

        assertEquals("Golf", vehicle.getCustomName());
        assertEquals(1, vehicle.getOwnerId());
        assertEquals("Volkswagen", vehicle.getMake());
        assertEquals("Golf V", vehicle.getModel());

        Vehicle vehicle2 = vehicles.get(1);

        assertEquals("Ford", vehicle2.getCustomName());
        assertEquals(2, vehicle2.getOwnerId());
        assertEquals("Ford", vehicle2.getMake());
        assertEquals("Focus MK4", vehicle2.getModel());
    }

    @Test
    public void testSave() throws CustomSQLException {
        //Create Record object
        Vehicle vehicle = new Vehicle();
        vehicle.setCustomName("Rust bucket");
        vehicle.setOwnerId(2);
        vehicle.setMake("Ford");
        vehicle.setModel("Escort");

        //Save it
        vehicleDao.save(vehicle);

        //Get the latest record from DB and compare it with object that was saved using vehicleDao
        Vehicle databaseVehicle = vehicleDao.get(3);
        assertEquals(3, databaseVehicle.getId());
        assertEquals(vehicle.getCustomName(), databaseVehicle.getCustomName());
        assertEquals(vehicle.getOwnerId(), databaseVehicle.getOwnerId());
        assertEquals(vehicle.getMake(), databaseVehicle.getMake());
        assertEquals(vehicle.getModel(), databaseVehicle.getModel());
    }

    @Test
    public void testUpdate() throws CustomSQLException {
        //Get an existing record and modify it
        Vehicle vehicle = vehicleDao.get(2);
        vehicle.setCustomName("Focus");

        //Update it in DB
        vehicleDao.update(vehicle);

        //Get vehicle's updated version and compare it
        Vehicle updatedVehicle = vehicleDao.get(2);
        assertEquals(vehicle.getId(), updatedVehicle.getId());
        assertEquals(vehicle.getCustomName(), updatedVehicle.getCustomName());
        assertEquals(vehicle.getOwnerId(), updatedVehicle.getOwnerId());
        assertEquals(vehicle.getMake(), updatedVehicle.getMake());
        assertEquals(vehicle.getModel(), updatedVehicle.getModel());
    }

    @Test
    public void testDelete() throws CustomSQLException {
        //Delete one vehicle
        Vehicle vehicleForDeletion = vehicleDao.get(2);
        vehicleDao.delete(vehicleForDeletion);

        //Check if it was deleted
        assertEquals(1, vehicleDao.getAll().size());
        assertNotEquals(vehicleForDeletion.getId(), vehicleDao.getAll().get(0).getId());
    }

}
