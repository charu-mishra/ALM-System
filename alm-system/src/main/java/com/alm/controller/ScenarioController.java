package com.alm.controller;

import com.alm.database.DBConnection;
import com.alm.model.Scenario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ScenarioController {
    private static final String BASE_SELECT = "SELECT scenario_id, scenario_name, interest_rate_shift_bp, liquidity_shock_pct, credit_shock_pct, scenario_date, description FROM scenarios";

    public int create(Scenario scenario) throws SQLException {
        Objects.requireNonNull(scenario, "scenario must not be null");
        String sql = "INSERT INTO scenarios (scenario_name, interest_rate_shift_bp, liquidity_shock_pct, credit_shock_pct, scenario_date, description) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, scenario.getScenarioName());
            statement.setInt(2, scenario.getInterestRateShiftBp());
            JdbcValue.setBigDecimal(statement, 3, scenario.getLiquidityShockPct());
            JdbcValue.setBigDecimal(statement, 4, scenario.getCreditShockPct());
            JdbcValue.setLocalDate(statement, 5, scenario.getScenarioDate());
            statement.setString(6, scenario.getDescription());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating Scenario failed; no rows were affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    scenario.setScenarioId(generatedId);
                    return generatedId;
                }
            }
            throw new SQLException("Creating Scenario failed; no generated key was returned.");
        }
    }

    public Optional<Scenario> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE scenario_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapRow(resultSet)) : Optional.empty();
            }
        }
    }

    public List<Scenario> findAll() throws SQLException {
        return queryList(BASE_SELECT + " ORDER BY scenario_date DESC, scenario_id DESC", statement -> { });
    }

    public boolean update(Scenario scenario) throws SQLException {
        Objects.requireNonNull(scenario, "scenario must not be null");
        String sql = "UPDATE scenarios SET scenario_name = ?, interest_rate_shift_bp = ?, liquidity_shock_pct = ?, credit_shock_pct = ?, scenario_date = ?, description = ? WHERE scenario_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, scenario.getScenarioName());
            statement.setInt(2, scenario.getInterestRateShiftBp());
            JdbcValue.setBigDecimal(statement, 3, scenario.getLiquidityShockPct());
            JdbcValue.setBigDecimal(statement, 4, scenario.getCreditShockPct());
            JdbcValue.setLocalDate(statement, 5, scenario.getScenarioDate());
            statement.setString(6, scenario.getDescription());
            statement.setInt(7, scenario.getScenarioId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM scenarios WHERE scenario_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    private List<Scenario> queryList(String sql, StatementBinder binder) throws SQLException {
        List<Scenario> items = new ArrayList<>();
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

    private Scenario mapRow(ResultSet resultSet) throws SQLException {
        Scenario scenario = new Scenario();
        scenario.setScenarioId(resultSet.getInt("scenario_id"));
        scenario.setScenarioName(resultSet.getString("scenario_name"));
        scenario.setInterestRateShiftBp(resultSet.getInt("interest_rate_shift_bp"));
        scenario.setLiquidityShockPct(resultSet.getBigDecimal("liquidity_shock_pct"));
        scenario.setCreditShockPct(resultSet.getBigDecimal("credit_shock_pct"));
        scenario.setScenarioDate(JdbcValue.getLocalDate(resultSet, "scenario_date"));
        scenario.setDescription(resultSet.getString("description"));
        return scenario;
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
