package com.n3lx.dao;

import com.n3lx.jdbc.JdbcConnectionFactory;
import com.n3lx.exception.CustomSQLException;
import com.n3lx.model.User;
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
public class UserDaoTest {

    @Autowired
    GenericDao<User> userDao;

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
        User user = userDao.get(1);

        assertEquals("John Doe", user.getUserName());

        User user2 = userDao.get(2);

        assertEquals("Jan Kowalski", user2.getUserName());
    }

    @Test
    public void testGetAll() throws CustomSQLException {
        List<User> users = userDao.getAll();

        //Check list size
        assertEquals(2, users.size());

        //Verify individual records from the list
        User user = users.get(0);

        assertEquals("John Doe", user.getUserName());

        User user2 = users.get(1);

        assertEquals("Jan Kowalski", user2.getUserName());
    }

    @Test
    public void testSave() throws CustomSQLException {
        //Create Record object
        User user = new User();
        user.setUserName("Elisabeth Smith");

        //Save it
        userDao.save(user);

        //Get the latest record from DB and compare it with object that was saved using userDao
        User databaseUser = userDao.get(3);
        assertEquals(3, databaseUser.getId());
        assertEquals(user.getUserName(), databaseUser.getUserName());
    }

    @Test
    public void testUpdate() throws CustomSQLException {
        //Get an existing record and modify it
        User user = userDao.get(2);
        user.setUserName("Anna Nowak");

        //Update it in DB
        userDao.update(user);

        //Get user's updated version and compare it
        User updatedUser = userDao.get(2);
        assertEquals(user.getId(), updatedUser.getId());
        assertEquals(user.getUserName(), updatedUser.getUserName());
    }

    @Test
    public void testDelete() throws CustomSQLException {
        //Create a user that has no vehicles so that they are easier to delete
        try (var connection = jdbcConnectionFactory.getConnection()) {
            var statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO users (user_name) VALUES ('James Holmes');");
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }

        //Delete one user
        User userForDeletion = userDao.get(3);
        userDao.delete(userForDeletion);

        //Check if it was deleted
        assertEquals(2, userDao.getAll().size());
        assertNotEquals(userForDeletion.getId(), userDao.getAll().get(0).getId());
        assertNotEquals(userForDeletion.getId(), userDao.getAll().get(1).getId());
    }

}
