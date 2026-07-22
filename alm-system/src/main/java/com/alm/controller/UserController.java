package com.alm.controller;

import com.alm.database.DBConnection;
import com.alm.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.time.LocalDateTime;

public class UserController {
    private static final String BASE_SELECT = "SELECT user_id, username, password_hash, role, created_at FROM users";

    public int create(User user) throws SQLException {
        Objects.requireNonNull(user, "user must not be null");
        if (user.getCreatedAt() == null) {
                    user.setCreatedAt(LocalDateTime.now());
                }
        String sql = "INSERT INTO users (username, password_hash, role, created_at) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getRole());
            JdbcValue.setLocalDateTime(statement, 4, user.getCreatedAt());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating User failed; no rows were affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    user.setUserId(generatedId);
                    return generatedId;
                }
            }
            throw new SQLException("Creating User failed; no generated key was returned.");
        }
    }

    public Optional<User> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapRow(resultSet)) : Optional.empty();
            }
        }
    }

    public List<User> findAll() throws SQLException {
        return queryList(BASE_SELECT + " ORDER BY username", statement -> { });
    }

    public boolean update(User user) throws SQLException {
        Objects.requireNonNull(user, "user must not be null");
        String sql = "UPDATE users SET username = ?, password_hash = ?, role = ?, created_at = ? WHERE user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getRole());
            JdbcValue.setLocalDateTime(statement, 4, user.getCreatedAt());
            statement.setInt(5, user.getUserId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = BASE_SELECT + " WHERE username = ?";
        List<User> users = queryList(sql, statement -> statement.setString(1, username));
        return users.stream().findFirst();
    }

    /**
     * Compares an already-hashed password value. Hashing should be performed by the service layer.
     */
    public Optional<User> authenticateByHash(String username, String passwordHash) throws SQLException {
        String sql = BASE_SELECT + " WHERE username = ? AND password_hash = ?";
        List<User> users = queryList(sql, statement -> {
            statement.setString(1, username);
            statement.setString(2, passwordHash);
        });
        return users.stream().findFirst();
    }

    private List<User> queryList(String sql, StatementBinder binder) throws SQLException {
        List<User> items = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.bind(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapRow(resultSet));
                }
            }
        }
        return items;
    }

    private User mapRow(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRole(resultSet.getString("role"));
        user.setCreatedAt(JdbcValue.getLocalDateTime(resultSet, "created_at"));
        return user;
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
