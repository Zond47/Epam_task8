package com.epam.rd.java.basic.practice8;

import com.epam.rd.java.basic.practice8.db.DBManager;
import com.epam.rd.java.basic.practice8.db.entity.Team;
import org.junit.*;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Part5StudentTest {
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
    private static Team team;

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
            String sql = "CREATE TABLE IF NOT EXISTS teams (\n" +
                    "  id INTEGER(11) NOT NULL AUTO_INCREMENT,\n" +
                    " name VARCHAR(20) NOT NULL, \n" +
                    "  PRIMARY KEY (id));";
            statement.executeUpdate(sql);
        }
        team = Team.createTeam("teamA");
        dbManager.insertTeam(team);
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
            String sql = "DROP TABLE IF EXISTS teams";
            statement.executeUpdate(sql);
        }
    }

    @Test
    public void testUpdateTeam() {
        team.setName("TeamX");
        dbManager.updateTeam(team);
        String expected = "TeamX";
        String actual = dbManager.getTeam("TeamX").getName();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDemo() {
        Demo.printList(dbManager.findAllTeams());
        String expected = "[teamA]\r\n";
        Assert.assertEquals(expected, outputStream.toString());
    }
}