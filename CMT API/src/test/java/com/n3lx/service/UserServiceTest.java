package com.n3lx.service;

import com.n3lx.exception.CustomSQLException;
import com.n3lx.exception.InputValidationException;
import com.n3lx.jdbc.JdbcConnectionFactory;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:servlet-context-test.xml")
public class UserServiceTest {

    @Autowired
    GenericService<User> userService;

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
        //Verify individual fields in the User object
        User user = userService.get(1);

        assertEquals("John Doe", user.getUserName());
    }

    @Test
    public void testGetWithNegativeId() {
        assertThrows(InputValidationException.class, () -> userService.get(-1));
    }

    @Test
    public void testGetAll() throws Exception {
        List<User> users = userService.getAll();

        //Check list size
        assertEquals(2, users.size());

        //Verify individual users from the list
        User user = users.get(0);

        assertEquals("John Doe", user.getUserName());

        User user2 = users.get(1);

        assertEquals("Jan Kowalski", user2.getUserName());
    }

    @Test
    public void testSave() throws Exception {
        //Create User object
        User user = new User();
        user.setUserName("Elisabeth Smith");

        //Save it
        userService.save(user);

        //Get the latest user from DB and compare it with object that was saved using userService
        User databaseUser = userService.get(3);
        assertEquals(3, databaseUser.getId());
        assertEquals(user.getUserName(), databaseUser.getUserName());
    }

    @Test
    public void testSaveWithInvalidUsername() {
        //Create User object
        User user = new User();

        StringBuilder username = new StringBuilder();
        for (int x = 0; x < 40; x++) {
            username.append("a");
        }
        user.setUserName(username.toString());

        //Try to save it
        assertThrows(InputValidationException.class, () -> userService.save(user));
    }

    @Test
    public void testSaveWithNullUser() {
        //Create User object
        User user = null;

        //Try to save it
        assertThrows(InputValidationException.class, () -> userService.save(user));
    }

    @Test
    public void testSaveWithNullValues() {
        //Create User object
        User user = new User();
        user.setUserName(null);

        //Test for null username
        assertThrows(InputValidationException.class, () -> userService.save(user));
    }

    @Test
    public void testUpdate() throws Exception {
        //Modify existing User object
        User user = userService.get(2);
        user.setUserName("Adam Nowak");

        //Save it
        userService.update(user);

        //Get the latest user from DB and compare it with object that was saved using userService
        User databaseUser = userService.get(2);
        assertEquals(user.getId(), databaseUser.getId());
        assertEquals(user.getUserName(), databaseUser.getUserName());
    }

    @Test
    public void testUpdateWithInvalidUsername() throws Exception {
        //Modify existing User object
        User user = userService.get(2);

        StringBuilder username = new StringBuilder();
        for (int x = 0; x < 40; x++) {
            username.append("a");
        }
        user.setUserName(username.toString());

        //Try to save it
        assertThrows(InputValidationException.class, () -> userService.update(user));
    }

    @Test
    public void testUpdateWithNullUser() {
        //Modify existing User object
        User user = null;

        //Try to save it
        assertThrows(InputValidationException.class, () -> userService.update(user));
    }

    @Test
    public void testUpdateWithNullValues() throws Exception {
        //Modify existing User object
        User user = userService.get(2);
        user.setUserName(null);

        //Test for null username
        assertThrows(InputValidationException.class, () -> userService.update(user));
    }

    @Test
    public void testDelete() throws Exception {
        //Create a user that has no vehicles so that they are easier to delete
        try (var connection = jdbcConnectionFactory.getConnection()) {
            var statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO users (user_name) VALUES ('James Holmes');");
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }

        //Delete one user
        User userForDeletion = userService.get(3);
        userService.delete(userForDeletion);

        //Check if it was deleted
        assertEquals(2, userService.getAll().size());
        assertNotEquals(userForDeletion.getId(), userService.getAll().get(0).getId());
        assertNotEquals(userForDeletion.getId(), userService.getAll().get(1).getId());
    }

    @Test
    public void testDeleteWithNullRecord() throws Exception {
        //Try to delete one user
        User userForDeletion = null;
        assertThrows(InputValidationException.class, () -> userService.delete(userForDeletion));
    }

    @Test
    public void testDeleteWithNullValues() throws Exception {
        //Create a user that has no vehicles so that they are easier to delete
        try (var connection = jdbcConnectionFactory.getConnection()) {
            var statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO users (user_name) VALUES ('James Holmes');");
        } catch (SQLException e) {
            throw new CustomSQLException(e.getMessage(), e.getSQLState());
        }
        //Pull a user from DB
        User user = userService.get(3);

        //Test for null username
        user.setUserName(null);
        assertThrows(InputValidationException.class, () -> userService.delete(user));
    }

}
