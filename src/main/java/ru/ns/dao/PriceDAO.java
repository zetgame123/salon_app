package ru.ns.dao;

import ru.ns.database.DatabaseManager;
import ru.ns.model.Price;
import ru.ns.model.PriceListItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
                       p.id_haircut_type,
                       p.price,
                       p.start_date,
                       ht.name AS haircut_name,
                       ht.gender
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

    public List<PriceListItem> findActualWithHaircut(
            LocalDate onDate) {

        List<PriceListItem> items = new ArrayList<>();

        String sql = """
                SELECT p.id_price,
                       p.id_haircut_type,
                       p.price,
                       p.start_date,
                       ht.name AS haircut_name,
                       ht.gender
                FROM prices p
                JOIN haircut_types ht
                    ON p.id_haircut_type = ht.id_haircut_type
                JOIN (
                    SELECT id_haircut_type,
                           MAX(start_date) AS max_date
                    FROM prices
                    WHERE start_date <= ?
                    GROUP BY id_haircut_type
                ) latest
                    ON p.id_haircut_type = latest.id_haircut_type
                   AND p.start_date = latest.max_date
                ORDER BY ht.name
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(onDate));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                items.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public Price findById(int idPrice) {

        String sql = """
                SELECT *
                FROM prices
                WHERE id_price = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, idPrice);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapPrice(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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

    public boolean save(Price price) {

        String sql = """
                INSERT INTO prices
                (id_haircut_type, start_date, price)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, price.getIdHaircutType());
            ps.setDate(
                    2,
                    Date.valueOf(price.getStartDate()));
            ps.setBigDecimal(3, price.getPrice());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(Price price) {

        String sql = """
                UPDATE prices
                SET id_haircut_type = ?,
                    start_date = ?,
                    price = ?
                WHERE id_price = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, price.getIdHaircutType());
            ps.setDate(
                    2,
                    Date.valueOf(price.getStartDate()));
            ps.setBigDecimal(3, price.getPrice());
            ps.setInt(4, price.getIdPrice());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int idPrice) {

        String sql = """
                DELETE FROM prices
                WHERE id_price = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, idPrice);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private PriceListItem mapRow(ResultSet rs)
            throws SQLException {

        PriceListItem item = new PriceListItem();

        item.setIdPrice(rs.getInt("id_price"));
        item.setIdHaircutType(rs.getInt("id_haircut_type"));
        item.setHaircutName(rs.getString("haircut_name"));
        item.setGender(rs.getString("gender"));
        item.setPrice(rs.getBigDecimal("price"));

        Date date = rs.getDate("start_date");

        if (date != null) {
            item.setStartDate(date.toLocalDate());
        }

        return item;
    }

    private Price mapPrice(ResultSet rs)
            throws SQLException {

        Price price = new Price();

        price.setIdPrice(rs.getInt("id_price"));
        price.setIdHaircutType(
                rs.getInt("id_haircut_type"));
        price.setPrice(rs.getBigDecimal("price"));

        Date date = rs.getDate("start_date");

        if (date != null) {
            price.setStartDate(date.toLocalDate());
        }

        return price;
    }
}
