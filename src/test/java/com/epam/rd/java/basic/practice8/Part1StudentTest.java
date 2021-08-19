package com.epam.rd.java.basic.practice8;

import com.epam.rd.java.basic.practice8.db.DBManager;
import com.epam.rd.java.basic.practice8.db.entity.User;
import org.junit.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Part1StudentTest {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:~/test";
    private static final String URL_CONNECTION = "jdbc:h2:~/test;user=youruser;password=yourpassword;";
    private static final String USER = "youruser";
    private static final String PASS = "yourpassword";

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalStream = System.out;

    @Before
    public void setOutputStream() {
        System.setOut(new PrintStream(outputStream));
    }

    @After
    public void restoreOutputStream() {
        System.setOut(originalStream);
    }

    private static DBManager dbManager;

    @BeforeClass
    public static void beforeTest() throws SQLException, ClassNotFoundException {
        Class.forName(JDBC_DRIVER);

        try (OutputStream output = new FileOutputStream("app.properties")) {
            Properties prop = new Properties();
            prop.setProperty("connection.url", URL_CONNECTION);
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }

        dbManager = DBManager.getInstance();

        try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement statement = con.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (\n" +
                    "  id INTEGER(11) NOT NULL AUTO_INCREMENT,\n" +
                    " login VARCHAR(20) NOT NULL, \n" +
                    "  PRIMARY KEY (id));";
            statement.executeUpdate(sql);
        }
    }

    @AfterClass
    public static void afterTest() throws SQLException {
        try (OutputStream output = new FileOutputStream("app.properties")) {
            Properties prop = new Properties();
            prop.setProperty("connection.url", "jdbc:mysql://localhost:3306/p8db?user=user&password=userpass");
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }
        try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement statement = con.createStatement()) {
            String sql = "DROP TABLE IF EXISTS users";
            statement.executeUpdate(sql);
        }
    }

    @Test
    public void testInsertUser() {
        User actual = User.createUser("petrov");
        dbManager.insertUser(actual);
        User expected = dbManager.getUser("petrov");
        Assert.assertEquals(expected.getId(), actual.getId());
    }

    @Test(expected = NullPointerException.class)
    public void testInsertNullUser() {
        User nullUser = null;
        dbManager.insertUser(nullUser);
    }

    @Test
    public void testFindAllUsers() {
        List<User> expected = new ArrayList<>();
        User user = User.createUser("Thor");
        expected.add(user);
        dbManager.insertUser(user);
        List<User> actual = dbManager.findAllUsers();
        Assert.assertEquals(expected.size(), actual.size());
    }

    @Test
    public void testShouldModifyUsersId() {
        User user = User.createUser("Freya");
        int expected_id = user.getId();
        dbManager.insertUser(user);
        int actual_id = dbManager.getUser("Freya").getId();
        Assert.assertNotEquals(expected_id, actual_id);
    }

    @Test
    public void testUserHascode() {
        User user = User.createUser("Tom");
        int expected = 84305;
        int actual = user.hashCode();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUserEquals() {
        User actual = User.createUser("Odin");
        User expected = new User();
        expected.setLogin("Odin");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUserLogin() {
        User user = new User();
        user.setLogin("Diana");
        String expected = "Diana";
        String actual = user.getLogin();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUserId() {
        User user = User.createUser("Loki");
        user.setId(1);
        int expected = 1;
        int actual = user.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUserToString() {
        User user = User.createUser("Freya");
        String expected = "Freya";
        String actual = user.toString();
        Assert.assertEquals(expected, actual);
    }
}