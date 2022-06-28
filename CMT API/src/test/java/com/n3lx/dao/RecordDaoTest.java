package com.n3lx.dao;

import com.n3lx.jdbc.JdbcConnectionFactory;
import com.n3lx.exception.CustomSQLException;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:servlet-context-test.xml")
public class RecordDaoTest {

    @Autowired
    GenericDao<Record> recordDao;

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
        Record record = recordDao.get(1);

        assertEquals(1, record.getId());
        assertEquals(1, record.getVehicleId());
        assertEquals(LocalDate.now(), record.getCreationDate());
        assertEquals(378000, record.getMileage());
        assertEquals("Clutch replacement", record.getShortDescription());
        assertEquals("Clutch and gearbox oil were replaced for total of 3500 PLN", record.getLongDescription());

        Record record2 = recordDao.get(2);
        assertEquals(2, record2.getId());
        assertEquals(1, record2.getVehicleId());
        assertEquals(LocalDate.now(), record2.getCreationDate());
        assertEquals(378500, record2.getMileage());
        assertEquals("Oil change", record2.getShortDescription());
        assertEquals("Engine oil change", record2.getLongDescription());
    }

    @Test
    public void testGetAll() throws CustomSQLException {
        List<Record> records = recordDao.getAll();

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
    public void testSave() throws CustomSQLException {
        //Create Record object
        Record record = new Record();
        record.setVehicleId(1);
        record.setCreationDate(LocalDate.now());
        record.setMileage(395000);
        record.setShortDescription("Front shocks replacement");
        record.setLongDescription("Both front shocks were replaced for 2500 PLN");

        //Save it
        recordDao.save(record);

        //Get the latest record from DB and compare it with object that was saved using recordDao
        Record databaseRecord = recordDao.get(3);
        assertEquals(3, databaseRecord.getId());
        assertEquals(record.getVehicleId(), databaseRecord.getVehicleId());
        assertEquals(record.getCreationDate(), databaseRecord.getCreationDate());
        assertEquals(record.getMileage(), databaseRecord.getMileage());
        assertEquals(record.getShortDescription(), databaseRecord.getShortDescription());
        assertEquals(record.getLongDescription(), databaseRecord.getLongDescription());
    }

    @Test
    public void testUpdate() throws CustomSQLException {
        //Get an existing record and modify it
        Record record = recordDao.get(2);
        record.setShortDescription("Modified description");

        //Update it in DB
        recordDao.update(record);

        //Get record's updated version and compare it
        Record updatedRecord = recordDao.get(2);
        assertEquals(record.getId(), updatedRecord.getId());
        assertEquals(record.getVehicleId(), updatedRecord.getVehicleId());
        assertEquals(record.getCreationDate(), updatedRecord.getCreationDate());
        assertEquals(record.getMileage(), updatedRecord.getMileage());
        assertEquals(record.getShortDescription(), updatedRecord.getShortDescription());
        assertEquals(record.getLongDescription(), updatedRecord.getLongDescription());
    }

    @Test
    public void testDelete() throws CustomSQLException {
        //Delete one record
        Record recordForDeletion = recordDao.get(2);
        recordDao.delete(recordForDeletion);

        //Check if it was deleted
        assertEquals(1,recordDao.getAll().size());
        assertNotEquals(recordForDeletion.getId(),recordDao.getAll().get(0).getId());
    }

}
