package ru.ns.dao;

import ru.ns.database.DatabaseManager;
import ru.ns.model.ServiceHistory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ServiceHistoryDAO {

    private final Connection connection;

    public ServiceHistoryDAO() {
        connection = DatabaseManager
                .getInstance()
                .getConnection();
    }

    public List<ServiceHistory> findAll() {

        List<ServiceHistory> history = new ArrayList<>();

        String sql = """
                SELECT *
                FROM service_history
                ORDER BY service_date DESC
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                history.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }

    private ServiceHistory mapRow(ResultSet rs)
            throws SQLException {

        ServiceHistory item = new ServiceHistory();

        item.setIdService(rs.getInt("id_service"));
        item.setSurname(rs.getString("surname"));
        item.setName(rs.getString("name"));
        item.setHaircut(rs.getString("haircut"));
        item.setBranch(rs.getString("branch"));
        item.setTotalCost(rs.getBigDecimal("total_cost"));
        item.setDiscountApplied(
                rs.getBoolean("discount_applied"));

        Timestamp timestamp =
                rs.getTimestamp("service_date");

        if (timestamp != null) {
            item.setServiceDate(
                    timestamp.toLocalDateTime());
        }

        return item;
    }
}
