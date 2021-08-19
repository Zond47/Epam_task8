package com.epam.rd.java.basic.practice8.db;

import com.epam.rd.java.basic.practice8.db.entity.Team;
import com.epam.rd.java.basic.practice8.db.entity.User;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager for work with DB.
 */
public class DBManager {
    private static DBManager dbManager;
    private static String connectionUrl;
    private Connection connection;
    private static final Logger LOGGER = Logger.getLogger(DBManager.class.getName());

    private DBManager() {
    }

    public static DBManager getInstance() {
        if (dbManager == null) {
            dbManager = new DBManager();
            connectionUrl = getConnectionUrl();
        }
        return dbManager;
    }

    public Connection getConnection() {
        try {
            connection = DriverManager.getConnection(connectionUrl);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return connection;
    }

    private static String getConnectionUrl() {
        String connectionUrl = "";
        try (FileReader fileReader = new FileReader("app.properties")) {
            Properties properties = new Properties();
            properties.load(fileReader);
            connectionUrl = properties.getProperty("connection.url");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return connectionUrl;
    }

    public void updateTeam(Team team) {
        String sql = "UPDATE teams set name=? where id=?";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, team.getName());
            preparedStatement.setInt(2, team.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    public List<Team> findAllTeams() {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT name FROM teams ORDER BY id";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String name = resultSet.getString(1);
                    Team team = getTeam(name);
                    teams.add(team);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return teams;
    }

    public void deleteTeam(Team team) {
        String sql = "DELETE FROM teams WHERE id=?";
        try (Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, team.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT login FROM users ORDER BY id";
        try (Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String login = resultSet.getString(1);
                    User user = getUser(login);
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return users;
    }

    public List<Team> getUserTeams(User user) {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT team_id FROM users_teams where user_id=? ORDER BY team_id";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, user.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    Team team = getTeamByID(id);
                    teams.add(team);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return teams;
    }

    public void setTeamsForUser(User user, Team... teams) {
        String sql = "INSERT INTO users_teams (user_id, team_id) VALUES (?, ?);";
        int count;
        try (Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Team team : teams) {
                preparedStatement.setInt(1, user.getId());
                preparedStatement.setInt(2, team.getId());
                preparedStatement.addBatch();
            }
            count = preparedStatement.executeBatch().length;
            if (count == teams.length) {
                conn.commit();
            } else {
                conn.rollback();
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    public Team getTeam(String team) {
        Team returnedTeam = new Team();
        String sql = "SELECT id, name FROM teams WHERE name=?";
        try (Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, team);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    returnedTeam.setId(resultSet.getInt(1));
                    returnedTeam.setName(resultSet.getString(2));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return returnedTeam;
    }

    public Team getTeamByID(int id) {
        Team returnedTeam = new Team();
        String sql = "SELECT name FROM teams WHERE id=?";
        try (Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    returnedTeam.setId(id);
                    returnedTeam.setName(resultSet.getString(1));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return returnedTeam;
    }

    public User getUser(String user) {
        User returnedUser = new User();
        String sql = "SELECT id, login FROM users WHERE login=?";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, user);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    returnedUser.setId(resultSet.getInt(1));
                    returnedUser.setLogin(resultSet.getString(2));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return returnedUser;
    }

    public void insertTeam(Team team) {
        String sql = "INSERT INTO teams(name) VALUES(?)";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, team.getName());
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    team.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    public void insertUser(User user) {
        String sql = "INSERT INTO users(login) VALUES(?)";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }
}
