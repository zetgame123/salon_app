package ru.ns.dao;

import ru.ns.database.DatabaseManager;
import ru.ns.model.HaircutType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HaircutTypeDAO {

    private final Connection connection;

    public HaircutTypeDAO() {
        connection = DatabaseManager
                .getInstance()
                .getConnection();
    }

    public List<HaircutType> findAll() {

        List<HaircutType> types = new ArrayList<>();

        String sql = """
                SELECT *
                FROM haircut_types
                ORDER BY name
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                types.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return types;
    }

    public boolean save(HaircutType type) {

        String sql = """
                INSERT INTO haircut_types (name, gender)
                VALUES (?, ?)
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, type.getName());
            ps.setString(2, type.getGender());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private HaircutType mapRow(ResultSet rs)
            throws SQLException {

        HaircutType type = new HaircutType();

        type.setIdHaircutType(
                rs.getInt("id_haircut_type"));
        type.setName(rs.getString("name"));
        type.setGender(rs.getString("gender"));

        return type;
    }
}
