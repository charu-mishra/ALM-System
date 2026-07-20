package com.alm.controller;

import com.alm.database.DBConnection;
import com.alm.model.ReportHistory;

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

public class ReportHistoryController {
    private static final String BASE_SELECT = "SELECT report_id, report_name, report_type, generated_on, generated_by, summary FROM report_history";

    public int create(ReportHistory reportHistory) throws SQLException {
        Objects.requireNonNull(reportHistory, "reportHistory must not be null");
        if (reportHistory.getGeneratedOn() == null) {
                    reportHistory.setGeneratedOn(LocalDateTime.now());
                }
        String sql = "INSERT INTO report_history (report_name, report_type, generated_on, generated_by, summary) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, reportHistory.getReportName());
            statement.setString(2, reportHistory.getReportType());
            JdbcValue.setLocalDateTime(statement, 3, reportHistory.getGeneratedOn());
            JdbcValue.setNullableInt(statement, 4, reportHistory.getGeneratedBy());
            statement.setString(5, reportHistory.getSummary());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating ReportHistory failed; no rows were affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    reportHistory.setReportId(generatedId);
                    return generatedId;
                }
            }
            throw new SQLException("Creating ReportHistory failed; no generated key was returned.");
        }
    }

    public Optional<ReportHistory> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE report_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapRow(resultSet)) : Optional.empty();
            }
        }
    }

    public List<ReportHistory> findAll() throws SQLException {
        return queryList(BASE_SELECT + " ORDER BY generated_on DESC, report_id DESC", statement -> { });
    }

    public boolean update(ReportHistory reportHistory) throws SQLException {
        Objects.requireNonNull(reportHistory, "reportHistory must not be null");
        String sql = "UPDATE report_history SET report_name = ?, report_type = ?, generated_on = ?, generated_by = ?, summary = ? WHERE report_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reportHistory.getReportName());
            statement.setString(2, reportHistory.getReportType());
            JdbcValue.setLocalDateTime(statement, 3, reportHistory.getGeneratedOn());
            JdbcValue.setNullableInt(statement, 4, reportHistory.getGeneratedBy());
            statement.setString(5, reportHistory.getSummary());
            statement.setInt(6, reportHistory.getReportId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM report_history WHERE report_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    public List<ReportHistory> findByGeneratedBy(int userId) throws SQLException {
        String sql = BASE_SELECT + " WHERE generated_by = ? ORDER BY generated_on DESC, report_id DESC";
        return queryList(sql, statement -> {
            statement.setInt(1, userId);
        });
    }

    private List<ReportHistory> queryList(String sql, StatementBinder binder) throws SQLException {
        List<ReportHistory> items = new ArrayList<>();
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

    private ReportHistory mapRow(ResultSet resultSet) throws SQLException {
        ReportHistory reportHistory = new ReportHistory();
        reportHistory.setReportId(resultSet.getInt("report_id"));
        reportHistory.setReportName(resultSet.getString("report_name"));
        reportHistory.setReportType(resultSet.getString("report_type"));
        reportHistory.setGeneratedOn(JdbcValue.getLocalDateTime(resultSet, "generated_on"));
        reportHistory.setGeneratedBy(JdbcValue.getNullableInt(resultSet, "generated_by"));
        reportHistory.setSummary(resultSet.getString("summary"));
        return reportHistory;
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
