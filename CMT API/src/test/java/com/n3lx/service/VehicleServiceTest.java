package com.n3lx.service;

import com.n3lx.exception.InputValidationException;
import com.n3lx.jdbc.JdbcConnectionFactory;
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

import static org.junit.Assert.*;
import static org.junit.Assert.assertThrows;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:servlet-context-test.xml")
public class VehicleServiceTest {

    @Autowired
    GenericService<Vehicle> vehicleService;

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
    public void testGet() throws Exception {
        //Verify individual fields in the Vehicle object
        Vehicle vehicle = vehicleService.get(2);

        assertEquals("Ford", vehicle.getCustomName());
        assertEquals(2, vehicle.getOwnerId());
        assertEquals("Ford", vehicle.getMake());
        assertEquals("Focus MK4", vehicle.getModel());
    }

    @Test
    public void testGetWithNegativeId() {
        assertThrows(InputValidationException.class, () -> vehicleService.get(-1));
    }

    @Test
    public void testGetAll() throws Exception {
        List<Vehicle> vehicles = vehicleService.getAll();

        //Check list size
        assertEquals(2, vehicles.size());

        //Verify individual vehicles from the list
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
    public void testSave() throws Exception {
        //Create Vehicle object
        Vehicle vehicle = new Vehicle();
        vehicle.setCustomName("Rust bucket");
        vehicle.setOwnerId(2);
        vehicle.setMake("Ford");
        vehicle.setModel("Escort");

        //Save it
        vehicleService.save(vehicle);

        //Get the latest vehicle from DB and compare it with object that was saved using vehicleService
        Vehicle databaseVehicle = vehicleService.get(3);
        assertEquals(3, databaseVehicle.getId());
        assertEquals(vehicle.getCustomName(), databaseVehicle.getCustomName());
        assertEquals(vehicle.getOwnerId(), databaseVehicle.getOwnerId());
        assertEquals(vehicle.getMake(), databaseVehicle.getMake());
        assertEquals(vehicle.getModel(), databaseVehicle.getModel());
    }

    @Test
    public void testSaveWithInvalidMake() {
        //Create Vehicle object
        Vehicle vehicle = new Vehicle();
        vehicle.setCustomName("Rust bucket");
        vehicle.setOwnerId(2);

        StringBuilder make = new StringBuilder();
        for (int x = 0; x < 40; x++) {
            make.append("a");
        }
        vehicle.setMake(make.toString());

        vehicle.setModel("Escort");

        //Try to save it
        assertThrows(InputValidationException.class, () -> vehicleService.save(vehicle));
    }

    @Test
    public void testSaveWithInvalidModel() {
        //Create Vehicle object
        Vehicle vehicle = new Vehicle();
        vehicle.setCustomName("Rust bucket");
        vehicle.setOwnerId(2);
        vehicle.setMake("Ford");

        StringBuilder model = new StringBuilder();
        for (int x = 0; x < 40; x++) {
            model.append("a");
        }
        vehicle.setModel(model.toString());

        //Try to save it
        assertThrows(InputValidationException.class, () -> vehicleService.save(vehicle));
    }

    @Test
    public void testSaveWithInvalidCustomName() {
        //Create Vehicle object
        Vehicle vehicle = new Vehicle();

        StringBuilder customName = new StringBuilder();
        for (int x = 0; x < 40; x++) {
            customName.append("a");
        }
        vehicle.setCustomName(customName.toString());

        vehicle.setOwnerId(2);
        vehicle.setMake("Ford");
        vehicle.setModel("Escort");

        //Try to save it
        assertThrows(InputValidationException.class, () -> vehicleService.save(vehicle));
    }

    @Test
    public void testSaveWithNullVehicle() {
        //Create Vehicle object
        Vehicle vehicle = null;

        //Try to save it
        assertThrows(InputValidationException.class, () -> vehicleService.save(vehicle));
    }

    @Test
    public void testSaveWithNullValues() {
        //Create Vehicle object
        Vehicle vehicle = new Vehicle();
        vehicle.setCustomName("Rust bucket");
        vehicle.setOwnerId(2);
        vehicle.setMake("Ford");
        vehicle.setModel("Escort");

        //Test for null custom name
        vehicle.setCustomName(null);
        assertThrows(InputValidationException.class, () -> vehicleService.save(vehicle));
        vehicle.setCustomName(" ");

        //Test for null make
        vehicle.setMake(null);
        assertThrows(InputValidationException.class, () -> vehicleService.save(vehicle));
        vehicle.setMake(" ");

        //Test for null model
        vehicle.setModel(null);
        assertThrows(InputValidationException.class, () -> vehicleService.save(vehicle));
        vehicle.setModel(" ");
    }

    @Test
    public void testUpdate() throws Exception {
        //Modify an existing Vehicle object
        Vehicle vehicle = vehicleService.get(2);
        vehicle.setCustomName("The Car");

        //Update it
        vehicleService.update(vehicle);

        //Get the latest vehicle from DB and compare it with object that was saved using vehicleService
        Vehicle databaseVehicle = vehicleService.get(2);
        assertEquals(vehicle.getId(), databaseVehicle.getId());
        assertEquals(vehicle.getCustomName(), databaseVehicle.getCustomName());
        assertEquals(vehicle.getOwnerId(), databaseVehicle.getOwnerId());
        assertEquals(vehicle.getMake(), databaseVehicle.getMake());
        assertEquals(vehicle.getModel(), databaseVehicle.getModel());
    }

    @Test
    public void testUpdateWithInvalidCustomName() throws Exception {
        //Modify an existing Vehicle object
        Vehicle vehicle = vehicleService.get(2);

        StringBuilder customName = new StringBuilder();
        for (int x = 0; x < 40; x++) {
            customName.append("a");
        }
        vehicle.setCustomName(customName.toString());

        //Try to update it
        assertThrows(InputValidationException.class, () -> vehicleService.update(vehicle));
    }

    @Test
    public void testUpdateWithInvalidMake() throws Exception {
        //Modify an existing Vehicle object
        Vehicle vehicle = vehicleService.get(2);

        StringBuilder make = new StringBuilder();
        for (int x = 0; x < 40; x++) {
            make.append("a");
        }
        vehicle.setMake(make.toString());

        //Try to update it
        assertThrows(InputValidationException.class, () -> vehicleService.update(vehicle));
    }

    @Test
    public void testUpdateWithInvalidModel() throws Exception {
        //Modify an existing Vehicle object
        Vehicle vehicle = vehicleService.get(2);

        StringBuilder model = new StringBuilder();
        for (int x = 0; x < 40; x++) {
            model.append("a");
        }
        vehicle.setModel(model.toString());

        //Try to update it
        assertThrows(InputValidationException.class, () -> vehicleService.update(vehicle));
    }

    @Test
    public void testUpdateWithNullVehicle() {
        //Create Vehicle object
        Vehicle vehicle = null;

        //Try to update it
        assertThrows(InputValidationException.class, () -> vehicleService.update(vehicle));
    }

    @Test
    public void testUpdateWithNullValues() throws Exception {
        //Modify an existing Vehicle object
        Vehicle vehicle = vehicleService.get(2);

        //Test for null custom name
        vehicle.setCustomName(null);
        assertThrows(InputValidationException.class, () -> vehicleService.update(vehicle));
        vehicle.setCustomName(" ");

        //Test for null make
        vehicle.setMake(null);
        assertThrows(InputValidationException.class, () -> vehicleService.update(vehicle));
        vehicle.setMake(" ");

        //Test for null model
        vehicle.setModel(null);
        assertThrows(InputValidationException.class, () -> vehicleService.update(vehicle));
        vehicle.setModel(" ");
    }

    @Test
    public void testDelete() throws Exception {
        //Delete one vehicle
        Vehicle vehicleForDeletion = vehicleService.get(2);
        vehicleService.delete(vehicleForDeletion);

        //Check if it was deleted
        assertEquals(1, vehicleService.getAll().size());
        assertNotEquals(vehicleForDeletion.getId(), vehicleService.getAll().get(0).getId());
    }

    @Test
    public void testDeleteWithNullRecord() throws Exception {
        //Try to delete one vehicle
        Vehicle vehicleForDeletion = null;
        assertThrows(InputValidationException.class, () -> vehicleService.delete(vehicleForDeletion));
    }

    @Test
    public void testDeleteWithNullValues() throws Exception {
        //Pull a vehicle from DB
        Vehicle vehicle = vehicleService.get(2);

        //Test for null custom name
        vehicle.setCustomName(null);
        assertThrows(InputValidationException.class, () -> vehicleService.save(vehicle));
        vehicle.setCustomName(" ");

        //Test for null make
        vehicle.setMake(null);
        assertThrows(InputValidationException.class, () -> vehicleService.save(vehicle));
        vehicle.setMake(" ");

        //Test for null model
        vehicle.setModel(null);
        assertThrows(InputValidationException.class, () -> vehicleService.save(vehicle));
        vehicle.setModel(" ");
    }

}
