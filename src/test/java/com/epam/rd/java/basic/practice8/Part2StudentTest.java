package com.epam.rd.java.basic.practice8;

import com.epam.rd.java.basic.practice8.db.DBManager;
import com.epam.rd.java.basic.practice8.db.entity.Team;
import org.junit.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Part2StudentTest {
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
            String sql = "CREATE TABLE IF NOT EXISTS teams (\n" +
                    "  id INTEGER(11) NOT NULL AUTO_INCREMENT,\n" +
                    " name VARCHAR(20) NOT NULL, \n" +
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
            String sql = "DROP TABLE IF EXISTS teams";
            statement.executeUpdate(sql);
        }
    }

    @Test
    public void testInsertTeam() {
        Team actual = Team.createTeam("teamA");
        dbManager.insertTeam(actual);
        Team expected = dbManager.getTeam("teamA");
        Assert.assertEquals(expected.getName(), actual.getName());
    }

    @Test(expected = NullPointerException.class)
    public void testInsertNullTeam() {
        Team nullTeam = null;
        dbManager.insertTeam(nullTeam);
    }

    @Test
    public void testFindAllTeams() {
        List<Team> expected = new ArrayList<>();
        Team team1 = Team.createTeam("teamA");
        Team team2 = Team.createTeam("teamA");
        expected.add(team1);
        expected.add(team2);
        dbManager.insertTeam(team1);
        dbManager.insertTeam(team2);
        List<Team> actual = dbManager.findAllTeams();
        Assert.assertEquals(expected.size(), actual.size());
    }

    @Test
    public void testShouldModifyTeamsId() {
        Team team = Team.createTeam("teamA");
        int expected_id = team.getId();
        dbManager.insertTeam(team);
        int actual_id = dbManager.getTeam("teamA").getId();
        Assert.assertNotEquals(expected_id, actual_id);
    }

    @Test
    public void testTeamHascode() {
        Team team = Team.createTeam("Odin's team");
        int expected = -1486900778;
        int actual = team.hashCode();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testTeamEquals() {
        Team actual = Team.createTeam("Fenrir's team");
        Team expected = new Team();
        expected.setName("Fenrir's team");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testTeamName() {
        Team team = new Team();
        team.setName("Diana's team");
        String expected = "Diana's team";
        String actual = team.getName();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testTeamId() {
        Team team = Team.createTeam("Loki's team");
        team.setId(1);
        int expected = 1;
        int actual = team.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testTeamToString() {
        Team team = Team.createTeam("Freya's team");
        String expected = "Freya's team";
        String actual = team.toString();
        Assert.assertEquals(expected, actual);
    }
}