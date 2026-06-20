package ru.ns.dao;

import ru.ns.database.DatabaseManager;
import ru.ns.model.PriceListItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PriceDAO {

    private final Connection connection;

    public PriceDAO() {
        connection = DatabaseManager
                .getInstance()
                .getConnection();
    }

    public List<PriceListItem> findAllWithHaircut() {

        List<PriceListItem> items = new ArrayList<>();

        String sql = """
                SELECT p.id_price,
                       p.price,
                       p.start_date,
                       ht.name AS haircut_name
                FROM prices p
                JOIN haircut_types ht
                    ON p.id_haircut_type = ht.id_haircut_type
                ORDER BY ht.name, p.start_date DESC
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                items.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public BigDecimal findPriceById(int idPrice) {

        String sql = """
                SELECT price
                FROM prices
                WHERE id_price = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, idPrice);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("price");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private PriceListItem mapRow(ResultSet rs) throws SQLException {

        PriceListItem item = new PriceListItem();

        item.setIdPrice(rs.getInt("id_price"));
        item.setHaircutName(rs.getString("haircut_name"));
        item.setPrice(rs.getBigDecimal("price"));

        Date date = rs.getDate("start_date");

        if (date != null) {
            item.setStartDate(date.toLocalDate());
        }

        return item;
    }
}
