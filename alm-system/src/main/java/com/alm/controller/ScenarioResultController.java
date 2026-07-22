package com.alm.controller;

import com.alm.database.DBConnection;
import com.alm.model.ScenarioResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ScenarioResultController {
    private static final String BASE_SELECT = "SELECT result_id, scenario_id, metric_name, baseline_value, scenario_value, impact_value FROM scenario_results";

    public int create(ScenarioResult scenarioResult) throws SQLException {
        Objects.requireNonNull(scenarioResult, "scenarioResult must not be null");
        String sql = "INSERT INTO scenario_results (scenario_id, metric_name, baseline_value, scenario_value, impact_value) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, scenarioResult.getScenarioId());
            statement.setString(2, scenarioResult.getMetricName());
            JdbcValue.setBigDecimal(statement, 3, scenarioResult.getBaselineValue());
            JdbcValue.setBigDecimal(statement, 4, scenarioResult.getScenarioValue());
            JdbcValue.setBigDecimal(statement, 5, scenarioResult.getImpactValue());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating ScenarioResult failed; no rows were affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    scenarioResult.setResultId(generatedId);
                    return generatedId;
                }
            }
            throw new SQLException("Creating ScenarioResult failed; no generated key was returned.");
        }
    }

    public Optional<ScenarioResult> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE result_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapRow(resultSet)) : Optional.empty();
            }
        }
    }

    public List<ScenarioResult> findAll() throws SQLException {
        return queryList(BASE_SELECT + " ORDER BY scenario_id, result_id", statement -> { });
    }

    public boolean update(ScenarioResult scenarioResult) throws SQLException {
        Objects.requireNonNull(scenarioResult, "scenarioResult must not be null");
        String sql = "UPDATE scenario_results SET scenario_id = ?, metric_name = ?, baseline_value = ?, scenario_value = ?, impact_value = ? WHERE result_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, scenarioResult.getScenarioId());
            statement.setString(2, scenarioResult.getMetricName());
            JdbcValue.setBigDecimal(statement, 3, scenarioResult.getBaselineValue());
            JdbcValue.setBigDecimal(statement, 4, scenarioResult.getScenarioValue());
            JdbcValue.setBigDecimal(statement, 5, scenarioResult.getImpactValue());
            statement.setInt(6, scenarioResult.getResultId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM scenario_results WHERE result_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    public List<ScenarioResult> findByScenarioId(int scenarioId) throws SQLException {
        String sql = BASE_SELECT + " WHERE scenario_id = ? ORDER BY result_id";
        return queryList(sql, statement -> {
            statement.setInt(1, scenarioId);
        });
    }

    private List<ScenarioResult> queryList(String sql, StatementBinder binder) throws SQLException {
        List<ScenarioResult> items = new ArrayList<>();
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

    private ScenarioResult mapRow(ResultSet resultSet) throws SQLException {
        ScenarioResult scenarioResult = new ScenarioResult();
        scenarioResult.setResultId(resultSet.getInt("result_id"));
        scenarioResult.setScenarioId(resultSet.getInt("scenario_id"));
        scenarioResult.setMetricName(resultSet.getString("metric_name"));
        scenarioResult.setBaselineValue(resultSet.getBigDecimal("baseline_value"));
        scenarioResult.setScenarioValue(resultSet.getBigDecimal("scenario_value"));
        scenarioResult.setImpactValue(resultSet.getBigDecimal("impact_value"));
        return scenarioResult;
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
