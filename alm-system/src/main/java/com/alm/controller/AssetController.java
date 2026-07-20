package com.alm.controller;

import com.alm.database.DBConnection;
import com.alm.model.Asset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AssetController {
    private static final String BASE_SELECT = "SELECT asset_id, asset_name, asset_type, principal_amount, interest_rate, rate_type, maturity_date, currency, duration, is_rate_sensitive, is_liquid, credit_status, asset_status FROM assets";

    public int create(Asset asset) throws SQLException {
        Objects.requireNonNull(asset, "asset must not be null");
        String sql = "INSERT INTO assets (asset_name, asset_type, principal_amount, interest_rate, rate_type, maturity_date, currency, duration, is_rate_sensitive, is_liquid, credit_status, asset_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, asset.getAssetName());
            statement.setString(2, asset.getAssetType());
            JdbcValue.setBigDecimal(statement, 3, asset.getPrincipalAmount());
            JdbcValue.setBigDecimal(statement, 4, asset.getInterestRate());
            statement.setString(5, asset.getRateType());
            JdbcValue.setLocalDate(statement, 6, asset.getMaturityDate());
            statement.setString(7, asset.getCurrency());
            JdbcValue.setBigDecimal(statement, 8, asset.getDuration());
            statement.setBoolean(9, asset.isRateSensitive());
            statement.setBoolean(10, asset.isLiquid());
            statement.setString(11, asset.getCreditStatus());
            statement.setString(12, asset.getAssetStatus());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating Asset failed; no rows were affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    asset.setAssetId(generatedId);
                    return generatedId;
                }
            }
            throw new SQLException("Creating Asset failed; no generated key was returned.");
        }
    }

    public Optional<Asset> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE asset_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapRow(resultSet)) : Optional.empty();
            }
        }
    }

    public List<Asset> findAll() throws SQLException {
        return queryList(BASE_SELECT + " ORDER BY asset_id", statement -> { });
    }

    public boolean update(Asset asset) throws SQLException {
        Objects.requireNonNull(asset, "asset must not be null");
        String sql = "UPDATE assets SET asset_name = ?, asset_type = ?, principal_amount = ?, interest_rate = ?, rate_type = ?, maturity_date = ?, currency = ?, duration = ?, is_rate_sensitive = ?, is_liquid = ?, credit_status = ?, asset_status = ? WHERE asset_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, asset.getAssetName());
            statement.setString(2, asset.getAssetType());
            JdbcValue.setBigDecimal(statement, 3, asset.getPrincipalAmount());
            JdbcValue.setBigDecimal(statement, 4, asset.getInterestRate());
            statement.setString(5, asset.getRateType());
            JdbcValue.setLocalDate(statement, 6, asset.getMaturityDate());
            statement.setString(7, asset.getCurrency());
            JdbcValue.setBigDecimal(statement, 8, asset.getDuration());
            statement.setBoolean(9, asset.isRateSensitive());
            statement.setBoolean(10, asset.isLiquid());
            statement.setString(11, asset.getCreditStatus());
            statement.setString(12, asset.getAssetStatus());
            statement.setInt(13, asset.getAssetId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM assets WHERE asset_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    public List<Asset> findByStatus(String status) throws SQLException {
        String sql = BASE_SELECT + " WHERE asset_status = ? ORDER BY asset_id";
        return queryList(sql, statement -> {
            statement.setString(1, status);
        });
    }

    public List<Asset> findByType(String type) throws SQLException {
        String sql = BASE_SELECT + " WHERE asset_type = ? ORDER BY asset_id";
        return queryList(sql, statement -> {
            statement.setString(1, type);
        });
    }

    private List<Asset> queryList(String sql, StatementBinder binder) throws SQLException {
        List<Asset> items = new ArrayList<>();
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

    private Asset mapRow(ResultSet resultSet) throws SQLException {
        Asset asset = new Asset();
        asset.setAssetId(resultSet.getInt("asset_id"));
        asset.setAssetName(resultSet.getString("asset_name"));
        asset.setAssetType(resultSet.getString("asset_type"));
        asset.setPrincipalAmount(resultSet.getBigDecimal("principal_amount"));
        asset.setInterestRate(resultSet.getBigDecimal("interest_rate"));
        asset.setRateType(resultSet.getString("rate_type"));
        asset.setMaturityDate(JdbcValue.getLocalDate(resultSet, "maturity_date"));
        asset.setCurrency(resultSet.getString("currency"));
        asset.setDuration(resultSet.getBigDecimal("duration"));
        asset.setRateSensitive(resultSet.getBoolean("is_rate_sensitive"));
        asset.setLiquid(resultSet.getBoolean("is_liquid"));
        asset.setCreditStatus(resultSet.getString("credit_status"));
        asset.setAssetStatus(resultSet.getString("asset_status"));
        return asset;
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
