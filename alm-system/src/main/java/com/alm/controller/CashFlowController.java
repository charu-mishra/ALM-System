package com.alm.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.alm.database.DBConnection;
import com.alm.model.CashFlow;

@Repository
public class CashFlowController {
    private static final String BASE_SELECT = "SELECT flow_id, asset_id, liability_id, flow_date, amount, flow_type FROM cash_flows";

    private final AssetController assetDao;
    private final LiabilityController liabilityDao;

    public CashFlowController(AssetController assetDao, LiabilityController liabilityDao) {
        this.assetDao = assetDao;
        this.liabilityDao = liabilityDao;
    }

    public int create(CashFlow cashFlow) throws SQLException {
        Objects.requireNonNull(cashFlow, "cashFlow must not be null");
        validateCashFlowReferences(cashFlow);
        String sql = "INSERT INTO cash_flows (asset_id, liability_id, flow_date, amount, flow_type) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, new String[] {"flow_id"})) {
            JdbcValue.setNullableInt(statement, 1, cashFlow.getAssetId());
            JdbcValue.setNullableInt(statement, 2, cashFlow.getLiabilityId());
            JdbcValue.setLocalDate(statement, 3, cashFlow.getFlowDate());
            JdbcValue.setBigDecimal(statement, 4, cashFlow.getAmount());
            statement.setString(5, cashFlow.getFlowType());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating CashFlow failed; no rows were affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    cashFlow.setFlowId(generatedId);
                    return generatedId;
                }
            }
            throw new SQLException("Creating CashFlow failed; no generated key was returned.");
        }
    }

    public Optional<CashFlow> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE flow_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapRow(resultSet)) : Optional.empty();
            }
        }
    }

    public List<CashFlow> findAll() throws SQLException {
        return queryList(BASE_SELECT + " ORDER BY flow_date DESC, flow_id DESC", statement -> { });
    }

    public boolean update(CashFlow cashFlow) throws SQLException {
        Objects.requireNonNull(cashFlow, "cashFlow must not be null");
        validateCashFlowReferences(cashFlow);
        String sql = "UPDATE cash_flows SET asset_id = ?, liability_id = ?, flow_date = ?, amount = ?, flow_type = ? WHERE flow_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            JdbcValue.setNullableInt(statement, 1, cashFlow.getAssetId());
            JdbcValue.setNullableInt(statement, 2, cashFlow.getLiabilityId());
            JdbcValue.setLocalDate(statement, 3, cashFlow.getFlowDate());
            JdbcValue.setBigDecimal(statement, 4, cashFlow.getAmount());
            statement.setString(5, cashFlow.getFlowType());
            statement.setInt(6, cashFlow.getFlowId());
            return statement.executeUpdate() == 1;
        }
    }

    private void validateCashFlowReferences(CashFlow cashFlow) throws SQLException {
        Integer assetId = cashFlow.getAssetId();
        Integer liabilityId = cashFlow.getLiabilityId();

        if (assetId == null && liabilityId == null) {
            throw new IllegalArgumentException("cashFlow must reference either an assetId or a liabilityId");
        }
        if (assetId != null && liabilityId != null) {
            throw new IllegalArgumentException("cashFlow must reference only one of assetId or liabilityId");
        }
        if (assetId != null && assetDao.findById(assetId).isEmpty()) {
            throw new IllegalArgumentException("Asset with id " + assetId + " does not exist");
        }
        if (liabilityId != null && liabilityDao.findById(liabilityId).isEmpty()) {
            throw new IllegalArgumentException("Liability with id " + liabilityId + " does not exist");
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM cash_flows WHERE flow_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    public List<CashFlow> findByAssetId(int assetId) throws SQLException {
        String sql = BASE_SELECT + " WHERE asset_id = ? ORDER BY flow_date DESC, flow_id DESC";
        return queryList(sql, statement -> {
            statement.setInt(1, assetId);
        });
    }

    public List<CashFlow> findByLiabilityId(int liabilityId) throws SQLException {
        String sql = BASE_SELECT + " WHERE liability_id = ? ORDER BY flow_date DESC, flow_id DESC";
        return queryList(sql, statement -> {
            statement.setInt(1, liabilityId);
        });
    }

    private List<CashFlow> queryList(String sql, StatementBinder binder) throws SQLException {
        List<CashFlow> items = new ArrayList<>();
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

    private CashFlow mapRow(ResultSet resultSet) throws SQLException {
        CashFlow cashFlow = new CashFlow();
        cashFlow.setFlowId(resultSet.getInt("flow_id"));
        cashFlow.setAssetId(JdbcValue.getNullableInt(resultSet, "asset_id"));
        cashFlow.setLiabilityId(JdbcValue.getNullableInt(resultSet, "liability_id"));
        cashFlow.setFlowDate(JdbcValue.getLocalDate(resultSet, "flow_date"));
        cashFlow.setAmount(resultSet.getBigDecimal("amount"));
        cashFlow.setFlowType(resultSet.getString("flow_type"));
        return cashFlow;
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
