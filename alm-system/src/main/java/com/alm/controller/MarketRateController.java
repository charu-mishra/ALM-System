package com.alm.controller;

import com.alm.database.DBConnection;
import com.alm.model.MarketRate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MarketRateController {
    private static final String BASE_SELECT = "SELECT rate_id, rate_date, rate_type, tenor_months, interest_rate, currency FROM market_rates";

    public int create(MarketRate marketRate) throws SQLException {
        Objects.requireNonNull(marketRate, "marketRate must not be null");
        String sql = "INSERT INTO market_rates (rate_date, rate_type, tenor_months, interest_rate, currency) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            JdbcValue.setLocalDate(statement, 1, marketRate.getRateDate());
            statement.setString(2, marketRate.getRateType());
            statement.setInt(3, marketRate.getTenorMonths());
            JdbcValue.setBigDecimal(statement, 4, marketRate.getInterestRate());
            statement.setString(5, marketRate.getCurrency());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating MarketRate failed; no rows were affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    marketRate.setRateId(generatedId);
                    return generatedId;
                }
            }
            throw new SQLException("Creating MarketRate failed; no generated key was returned.");
        }
    }

    public Optional<MarketRate> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE rate_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapRow(resultSet)) : Optional.empty();
            }
        }
    }

    public List<MarketRate> findAll() throws SQLException {
        return queryList(BASE_SELECT + " ORDER BY rate_date DESC, tenor_months", statement -> { });
    }

    public boolean update(MarketRate marketRate) throws SQLException {
        Objects.requireNonNull(marketRate, "marketRate must not be null");
        String sql = "UPDATE market_rates SET rate_date = ?, rate_type = ?, tenor_months = ?, interest_rate = ?, currency = ? WHERE rate_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            JdbcValue.setLocalDate(statement, 1, marketRate.getRateDate());
            statement.setString(2, marketRate.getRateType());
            statement.setInt(3, marketRate.getTenorMonths());
            JdbcValue.setBigDecimal(statement, 4, marketRate.getInterestRate());
            statement.setString(5, marketRate.getCurrency());
            statement.setInt(6, marketRate.getRateId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM market_rates WHERE rate_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    public List<MarketRate> findByCurrency(String currency) throws SQLException {
        String sql = BASE_SELECT + " WHERE currency = ? ORDER BY rate_date DESC, tenor_months";
        return queryList(sql, statement -> {
            statement.setString(1, currency);
        });
    }

    public List<MarketRate> findByTypeAndCurrency(String rateType, String currency) throws SQLException {
        String sql = BASE_SELECT + " WHERE rate_type = ? AND currency = ? ORDER BY rate_date DESC, tenor_months";
        return queryList(sql, statement -> {
            statement.setString(1, rateType);
                        statement.setString(2, currency);
        });
    }

    private List<MarketRate> queryList(String sql, StatementBinder binder) throws SQLException {
        List<MarketRate> items = new ArrayList<>();
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

    private MarketRate mapRow(ResultSet resultSet) throws SQLException {
        MarketRate marketRate = new MarketRate();
        marketRate.setRateId(resultSet.getInt("rate_id"));
        marketRate.setRateDate(JdbcValue.getLocalDate(resultSet, "rate_date"));
        marketRate.setRateType(resultSet.getString("rate_type"));
        marketRate.setTenorMonths(resultSet.getInt("tenor_months"));
        marketRate.setInterestRate(resultSet.getBigDecimal("interest_rate"));
        marketRate.setCurrency(resultSet.getString("currency"));
        return marketRate;
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
