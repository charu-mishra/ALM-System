package com.alm.controller;

import com.alm.database.DBConnection;
import com.alm.model.RiskMetric;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RiskMetricController {
    private static final String BASE_SELECT = "SELECT metric_id, scenario_id, reporting_date, total_assets, total_liabilities, net_interest_income, liquidity_ratio, duration_gap, credit_risk_score FROM risk_metrics";

    public int create(RiskMetric riskMetric) throws SQLException {
        Objects.requireNonNull(riskMetric, "riskMetric must not be null");
        String sql = "INSERT INTO risk_metrics (scenario_id, reporting_date, total_assets, total_liabilities, net_interest_income, liquidity_ratio, duration_gap, credit_risk_score) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            JdbcValue.setNullableInt(statement, 1, riskMetric.getScenarioId());
            JdbcValue.setLocalDate(statement, 2, riskMetric.getReportingDate());
            JdbcValue.setBigDecimal(statement, 3, riskMetric.getTotalAssets());
            JdbcValue.setBigDecimal(statement, 4, riskMetric.getTotalLiabilities());
            JdbcValue.setBigDecimal(statement, 5, riskMetric.getNetInterestIncome());
            JdbcValue.setBigDecimal(statement, 6, riskMetric.getLiquidityRatio());
            JdbcValue.setBigDecimal(statement, 7, riskMetric.getDurationGap());
            JdbcValue.setBigDecimal(statement, 8, riskMetric.getCreditRiskScore());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating RiskMetric failed; no rows were affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    riskMetric.setMetricId(generatedId);
                    return generatedId;
                }
            }
            throw new SQLException("Creating RiskMetric failed; no generated key was returned.");
        }
    }

    public Optional<RiskMetric> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE metric_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapRow(resultSet)) : Optional.empty();
            }
        }
    }

    public List<RiskMetric> findAll() throws SQLException {
        return queryList(BASE_SELECT + " ORDER BY reporting_date DESC, metric_id DESC", statement -> { });
    }

    public boolean update(RiskMetric riskMetric) throws SQLException {
        Objects.requireNonNull(riskMetric, "riskMetric must not be null");
        String sql = "UPDATE risk_metrics SET scenario_id = ?, reporting_date = ?, total_assets = ?, total_liabilities = ?, net_interest_income = ?, liquidity_ratio = ?, duration_gap = ?, credit_risk_score = ? WHERE metric_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            JdbcValue.setNullableInt(statement, 1, riskMetric.getScenarioId());
            JdbcValue.setLocalDate(statement, 2, riskMetric.getReportingDate());
            JdbcValue.setBigDecimal(statement, 3, riskMetric.getTotalAssets());
            JdbcValue.setBigDecimal(statement, 4, riskMetric.getTotalLiabilities());
            JdbcValue.setBigDecimal(statement, 5, riskMetric.getNetInterestIncome());
            JdbcValue.setBigDecimal(statement, 6, riskMetric.getLiquidityRatio());
            JdbcValue.setBigDecimal(statement, 7, riskMetric.getDurationGap());
            JdbcValue.setBigDecimal(statement, 8, riskMetric.getCreditRiskScore());
            statement.setInt(9, riskMetric.getMetricId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM risk_metrics WHERE metric_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    public List<RiskMetric> findByScenarioId(int scenarioId) throws SQLException {
        String sql = BASE_SELECT + " WHERE scenario_id = ? ORDER BY reporting_date DESC, metric_id DESC";
        return queryList(sql, statement -> {
            statement.setInt(1, scenarioId);
        });
    }

    private List<RiskMetric> queryList(String sql, StatementBinder binder) throws SQLException {
        List<RiskMetric> items = new ArrayList<>();
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

    private RiskMetric mapRow(ResultSet resultSet) throws SQLException {
        RiskMetric riskMetric = new RiskMetric();
        riskMetric.setMetricId(resultSet.getInt("metric_id"));
        riskMetric.setScenarioId(JdbcValue.getNullableInt(resultSet, "scenario_id"));
        riskMetric.setReportingDate(JdbcValue.getLocalDate(resultSet, "reporting_date"));
        riskMetric.setTotalAssets(resultSet.getBigDecimal("total_assets"));
        riskMetric.setTotalLiabilities(resultSet.getBigDecimal("total_liabilities"));
        riskMetric.setNetInterestIncome(resultSet.getBigDecimal("net_interest_income"));
        riskMetric.setLiquidityRatio(resultSet.getBigDecimal("liquidity_ratio"));
        riskMetric.setDurationGap(resultSet.getBigDecimal("duration_gap"));
        riskMetric.setCreditRiskScore(resultSet.getBigDecimal("credit_risk_score"));
        return riskMetric;
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
