package ru.ns.dao;

import ru.ns.database.DatabaseManager;
import ru.ns.model.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class ServiceDAO {

    private final Connection connection;
    private final PriceDAO priceDAO;

    public ServiceDAO() {
        connection = DatabaseManager
                .getInstance()
                .getConnection();
        priceDAO = new PriceDAO();
    }

    public Service findById(int id) {

        String sql = """
                SELECT *
                FROM services
                WHERE id_service = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean save(Service service) {

        applyPricing(service);

        String sql = """
                INSERT INTO services
                (
                    id_client,
                    id_branch,
                    id_price,
                    service_date,
                    total_cost,
                    discount_applied,
                    client_wishes
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            bindService(ps, service);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(Service service) {

        applyPricing(service);

        String sql = """
                UPDATE services
                SET id_client = ?,
                    id_branch = ?,
                    id_price = ?,
                    service_date = ?,
                    total_cost = ?,
                    discount_applied = ?,
                    client_wishes = ?
                WHERE id_service = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            bindService(ps, service);
            ps.setInt(8, service.getIdService());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int id) {

        String sql = """
                DELETE FROM services
                WHERE id_service = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void bindService(
            PreparedStatement ps,
            Service service) throws SQLException {

        ps.setInt(1, service.getIdClient());
        ps.setInt(2, service.getIdBranch());
        ps.setInt(3, service.getIdPrice());
        ps.setTimestamp(
                4,
                Timestamp.valueOf(
                        service.getServiceDate()));
        ps.setBigDecimal(5, service.getTotalCost());
        ps.setBoolean(6, service.isDiscountApplied());

        String wishes = service.getClientWishes();

        if (wishes == null || wishes.isBlank()) {
            ps.setNull(7, Types.LONGVARCHAR);
        } else {
            ps.setString(7, wishes);
        }
    }

    private void applyPricing(Service service) {

        boolean regular = isRegularClient(
                service.getIdClient());

        BigDecimal price = priceDAO.findPriceById(
                service.getIdPrice());

        if (price == null) {
            service.setTotalCost(BigDecimal.ZERO);
            service.setDiscountApplied(false);
            return;
        }

        if (regular) {
            service.setDiscountApplied(true);
            service.setTotalCost(
                    price.multiply(
                                    new BigDecimal("0.97"))
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP));
        } else {
            service.setDiscountApplied(false);
            service.setTotalCost(price);
        }
    }

    private boolean isRegularClient(int idClient) {

        String sql = """
                SELECT regular_client
                FROM clients
                WHERE id_client = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, idClient);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("regular_client");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Service mapRow(ResultSet rs)
            throws SQLException {

        Service service = new Service();

        service.setIdService(rs.getInt("id_service"));
        service.setIdClient(rs.getInt("id_client"));
        service.setIdBranch(rs.getInt("id_branch"));
        service.setIdPrice(rs.getInt("id_price"));
        service.setTotalCost(rs.getBigDecimal("total_cost"));
        service.setDiscountApplied(
                rs.getBoolean("discount_applied"));
        service.setClientWishes(
                rs.getString("client_wishes"));

        Timestamp timestamp =
                rs.getTimestamp("service_date");

        if (timestamp != null) {
            service.setServiceDate(
                    timestamp.toLocalDateTime());
        }

        return service;
    }
}
