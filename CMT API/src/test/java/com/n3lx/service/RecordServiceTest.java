package com.n3lx.service;

import com.n3lx.exception.InputValidationException;
import com.n3lx.jdbc.JdbcConnectionFactory;
import com.n3lx.model.Record;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:servlet-context-test.xml")
public class RecordServiceTest {

    @Autowired
    GenericService<Record> recordService;

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
        //Verify individual fields in the record
        Record record = recordService.get(1);

        assertEquals(1, record.getId());
        assertEquals(1, record.getVehicleId());
        assertEquals(LocalDate.now(), record.getCreationDate());
        assertEquals(378000, record.getMileage());
        assertEquals("Clutch replacement", record.getShortDescription());
        assertEquals("Clutch and gearbox oil were replaced for total of 3500 PLN", record.getLongDescription());
    }

    @Test
    public void testGetWithNegativeId() {
        assertThrows(InputValidationException.class, () -> recordService.get(-1));
    }

    @Test
    public void testGetAll() throws Exception {
        List<Record> records = recordService.getAll();

        //Check list size
        assertEquals(2, records.size());

        //Verify individual records from the list
        Record record = records.get(0);

        assertEquals(1, record.getId());
        assertEquals(1, record.getVehicleId());
        assertEquals(LocalDate.now(), record.getCreationDate());
        assertEquals(378000, record.getMileage());
        assertEquals("Clutch replacement", record.getShortDescription());
        assertEquals("Clutch and gearbox oil were replaced for total of 3500 PLN", record.getLongDescription());

        Record record2 = records.get(1);
        assertEquals(2, record2.getId());
        assertEquals(1, record2.getVehicleId());
        assertEquals(LocalDate.now(), record2.getCreationDate());
        assertEquals(378500, record2.getMileage());
        assertEquals("Oil change", record2.getShortDescription());
        assertEquals("Engine oil change", record2.getLongDescription());
    }

    @Test
    public void testSave() throws Exception {
        //Create Record object
        Record record = new Record();
        record.setVehicleId(1);
        record.setCreationDate(LocalDate.now());
        record.setMileage(395000);
        record.setShortDescription("Front shocks replacement");
        record.setLongDescription("Both front shocks were replaced for 2500 PLN");

        //Save it
        recordService.save(record);

        //Get the latest record from DB and compare it with object that was saved using recordDao
        Record databaseRecord = recordService.get(3);
        assertEquals(3, databaseRecord.getId());
        assertEquals(record.getVehicleId(), databaseRecord.getVehicleId());
        assertEquals(record.getCreationDate(), databaseRecord.getCreationDate());
        assertEquals(record.getMileage(), databaseRecord.getMileage());
        assertEquals(record.getShortDescription(), databaseRecord.getShortDescription());
        assertEquals(record.getLongDescription(), databaseRecord.getLongDescription());
    }

    @Test
    public void testSaveWithInvalidDate() {
        //Create Record object
        Record record = new Record();
        record.setVehicleId(1);
        record.setCreationDate(LocalDate.now().plus(1, ChronoUnit.DAYS));
        record.setMileage(395000);
        record.setShortDescription("Front shocks replacement");
        record.setLongDescription("Both front shocks were replaced for 2500 PLN");

        //Try to save it
        assertThrows(InputValidationException.class, () -> recordService.save(record));
    }

    @Test
    public void testSaveWithInvalidMileage() {
        //Create Record object
        Record record = new Record();
        record.setVehicleId(1);
        record.setCreationDate(LocalDate.now());
        record.setMileage(-123);
        record.setShortDescription("Front shocks replacement");
        record.setLongDescription("Both front shocks were replaced for 2500 PLN");

        //Try to save it
        assertThrows(InputValidationException.class, () -> recordService.save(record));
    }

    @Test
    public void testSaveWithInvalidShortDescription() {
        //Create Record object
        Record record = new Record();
        record.setVehicleId(1);
        record.setCreationDate(LocalDate.now());
        record.setMileage(395000);

        StringBuilder shortDescription = new StringBuilder();
        for (int x = 0; x < 40; x++) {
            shortDescription.append("a");
        }
        record.setShortDescription(shortDescription.toString());

        record.setLongDescription("Both front shocks were replaced for 2500 PLN");

        //Try to save it
        assertThrows(InputValidationException.class, () -> recordService.save(record));
    }

    @Test
    public void testSaveWithInvalidLongDescription() {
        //Create Record object
        Record record = new Record();
        record.setVehicleId(1);
        record.setCreationDate(LocalDate.now());
        record.setMileage(395000);
        record.setShortDescription("Front shocks replacement");

        StringBuilder longDescription = new StringBuilder();
        for (int x = 0; x < 4100; x++) {
            longDescription.append("b");
        }
        record.setLongDescription(longDescription.toString());

        //Try to save it
        assertThrows(InputValidationException.class, () -> recordService.save(record));
    }

    @Test
    public void testSaveWithNullRecord() {
        //Create Record object
        Record record = null;

        //Try to save it
        assertThrows(InputValidationException.class, () -> recordService.save(record));
    }

    @Test
    public void testSaveWithNullValues() {
        //Create Record object
        Record record = new Record();
        record.setVehicleId(1);
        record.setCreationDate(LocalDate.now());
        record.setMileage(395000);
        record.setShortDescription("Front shocks replacement");
        record.setLongDescription("Both front shocks were replaced for 2500 PLN");

        //Test for null creation date
        record.setCreationDate(null);
        assertThrows(InputValidationException.class, () -> recordService.save(record));
        record.setCreationDate(LocalDate.now());

        //Test for null short description
        record.setShortDescription(null);
        assertThrows(InputValidationException.class, () -> recordService.save(record));
        record.setShortDescription(" ");

        //Test for null long description
        record.setLongDescription(null);
        assertThrows(InputValidationException.class, () -> recordService.save(record));
    }

    @Test
    public void testUpdate() throws Exception {
        //Modify an existing Record object
        Record record = recordService.get(2);
        record.setShortDescription("Modified description");

        //Update it
        recordService.update(record);

        //Get the latest record from DB and compare it with object that was saved using recordDao
        Record updatedRecord = recordService.get(2);
        assertEquals(record.getId(), updatedRecord.getId());
        assertEquals(record.getVehicleId(), updatedRecord.getVehicleId());
        assertEquals(record.getCreationDate(), updatedRecord.getCreationDate());
        assertEquals(record.getMileage(), updatedRecord.getMileage());
        assertEquals(record.getShortDescription(), updatedRecord.getShortDescription());
        assertEquals(record.getLongDescription(), updatedRecord.getLongDescription());
    }

    @Test
    public void testUpdateWithInvalidDate() throws Exception {
        //Modify an existing Record object
        Record record = recordService.get(2);
        record.setCreationDate(LocalDate.now().plus(1, ChronoUnit.DAYS));

        //Try to update it
        assertThrows(InputValidationException.class, () -> recordService.update(record));
    }

    @Test
    public void testUpdateWithInvalidMileage() throws Exception {
        //Modify an existing Record object
        Record record = recordService.get(2);
        record.setMileage(-222);

        //Try to update it
        assertThrows(InputValidationException.class, () -> recordService.update(record));
    }

    @Test
    public void testUpdateWithInvalidShortDescription() throws Exception {
        //Modify an existing Record object
        Record record = recordService.get(2);

        StringBuilder shortDescription = new StringBuilder();
        for (int x = 0; x < 40; x++) {
            shortDescription.append("a");
        }
        record.setShortDescription(shortDescription.toString());

        //Try to update it
        assertThrows(InputValidationException.class, () -> recordService.update(record));
    }

    @Test
    public void testUpdateWithInvalidLongDescription() throws Exception {
        //Modify an existing Record object
        Record record = recordService.get(2);

        StringBuilder longDescription = new StringBuilder();
        for (int x = 0; x < 4100; x++) {
            longDescription.append("b");
        }
        record.setLongDescription(longDescription.toString());

        //Try to update it
        assertThrows(InputValidationException.class, () -> recordService.update(record));
    }

    @Test
    public void testUpdateWithNullRecord() {
        //Create Record object
        Record record = null;

        //Try to update it
        assertThrows(InputValidationException.class, () -> recordService.update(record));
    }

    @Test
    public void testUpdateWithNullValues() throws Exception {
        //Modify an existing Record object
        Record record = recordService.get(2);

        //Test for null creation date
        record.setCreationDate(null);
        assertThrows(InputValidationException.class, () -> recordService.update(record));
        record.setCreationDate(LocalDate.now());

        //Test for null short description
        record.setShortDescription(null);
        assertThrows(InputValidationException.class, () -> recordService.update(record));
        record.setShortDescription(" ");

        //Test for null long description
        record.setLongDescription(null);
        assertThrows(InputValidationException.class, () -> recordService.update(record));
    }

    @Test
    public void testDelete() throws Exception {
        //Delete one record
        Record recordForDeletion = recordService.get(2);
        recordService.delete(recordForDeletion);

        //Check if it was deleted
        assertEquals(1, recordService.getAll().size());
        assertNotEquals(recordForDeletion.getId(), recordService.getAll().get(0).getId());
    }

    @Test
    public void testDeleteWithNullRecord() throws Exception {
        //Try to delete one record
        Record recordForDeletion = null;
        assertThrows(InputValidationException.class, () -> recordService.delete(recordForDeletion));
    }

    @Test
    public void testDeleteWithNullValues() throws Exception {
        //Pull a record from DB
        Record record = recordService.get(2);

        //Test for null creation date
        record.setCreationDate(null);
        assertThrows(InputValidationException.class, () -> recordService.delete(record));
        record.setCreationDate(LocalDate.now());

        //Test for null short description
        record.setShortDescription(null);
        assertThrows(InputValidationException.class, () -> recordService.delete(record));
        record.setShortDescription(" ");

        //Test for null long description
        record.setLongDescription(null);
        assertThrows(InputValidationException.class, () -> recordService.delete(record));
    }

}
