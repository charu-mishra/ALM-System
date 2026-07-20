package com.alm.controller;

import com.alm.database.DBConnection;
import com.alm.model.Liability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LiabilityController {
    private static final String BASE_SELECT = "SELECT liability_id, liability_name, liability_type, principal_amount, interest_rate, rate_type, maturity_date, currency, duration, is_rate_sensitive, is_short_term, liability_status FROM liabilities";

    public int create(Liability liability) throws SQLException {
        Objects.requireNonNull(liability, "liability must not be null");
        String sql = "INSERT INTO liabilities (liability_name, liability_type, principal_amount, interest_rate, rate_type, maturity_date, currency, duration, is_rate_sensitive, is_short_term, liability_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, liability.getLiabilityName());
            statement.setString(2, liability.getLiabilityType());
            JdbcValue.setBigDecimal(statement, 3, liability.getPrincipalAmount());
            JdbcValue.setBigDecimal(statement, 4, liability.getInterestRate());
            statement.setString(5, liability.getRateType());
            JdbcValue.setLocalDate(statement, 6, liability.getMaturityDate());
            statement.setString(7, liability.getCurrency());
            JdbcValue.setBigDecimal(statement, 8, liability.getDuration());
            statement.setBoolean(9, liability.isRateSensitive());
            statement.setBoolean(10, liability.isShortTerm());
            statement.setString(11, liability.getLiabilityStatus());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating Liability failed; no rows were affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    liability.setLiabilityId(generatedId);
                    return generatedId;
                }
            }
            throw new SQLException("Creating Liability failed; no generated key was returned.");
        }
    }

    public Optional<Liability> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE liability_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapRow(resultSet)) : Optional.empty();
            }
        }
    }

    public List<Liability> findAll() throws SQLException {
        return queryList(BASE_SELECT + " ORDER BY liability_id", statement -> { });
    }

    public boolean update(Liability liability) throws SQLException {
        Objects.requireNonNull(liability, "liability must not be null");
        String sql = "UPDATE liabilities SET liability_name = ?, liability_type = ?, principal_amount = ?, interest_rate = ?, rate_type = ?, maturity_date = ?, currency = ?, duration = ?, is_rate_sensitive = ?, is_short_term = ?, liability_status = ? WHERE liability_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, liability.getLiabilityName());
            statement.setString(2, liability.getLiabilityType());
            JdbcValue.setBigDecimal(statement, 3, liability.getPrincipalAmount());
            JdbcValue.setBigDecimal(statement, 4, liability.getInterestRate());
            statement.setString(5, liability.getRateType());
            JdbcValue.setLocalDate(statement, 6, liability.getMaturityDate());
            statement.setString(7, liability.getCurrency());
            JdbcValue.setBigDecimal(statement, 8, liability.getDuration());
            statement.setBoolean(9, liability.isRateSensitive());
            statement.setBoolean(10, liability.isShortTerm());
            statement.setString(11, liability.getLiabilityStatus());
            statement.setInt(12, liability.getLiabilityId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM liabilities WHERE liability_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    public List<Liability> findByStatus(String status) throws SQLException {
        String sql = BASE_SELECT + " WHERE liability_status = ? ORDER BY liability_id";
        return queryList(sql, statement -> {
            statement.setString(1, status);
        });
    }

    public List<Liability> findByType(String type) throws SQLException {
        String sql = BASE_SELECT + " WHERE liability_type = ? ORDER BY liability_id";
        return queryList(sql, statement -> {
            statement.setString(1, type);
        });
    }

    private List<Liability> queryList(String sql, StatementBinder binder) throws SQLException {
        List<Liability> items = new ArrayList<>();
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

    private Liability mapRow(ResultSet resultSet) throws SQLException {
        Liability liability = new Liability();
        liability.setLiabilityId(resultSet.getInt("liability_id"));
        liability.setLiabilityName(resultSet.getString("liability_name"));
        liability.setLiabilityType(resultSet.getString("liability_type"));
        liability.setPrincipalAmount(resultSet.getBigDecimal("principal_amount"));
        liability.setInterestRate(resultSet.getBigDecimal("interest_rate"));
        liability.setRateType(resultSet.getString("rate_type"));
        liability.setMaturityDate(JdbcValue.getLocalDate(resultSet, "maturity_date"));
        liability.setCurrency(resultSet.getString("currency"));
        liability.setDuration(resultSet.getBigDecimal("duration"));
        liability.setRateSensitive(resultSet.getBoolean("is_rate_sensitive"));
        liability.setShortTerm(resultSet.getBoolean("is_short_term"));
        liability.setLiabilityStatus(resultSet.getString("liability_status"));
        return liability;
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
