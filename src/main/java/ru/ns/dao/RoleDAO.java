package ru.ns.dao;

import ru.ns.database.DatabaseManager;
import ru.ns.model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    private final Connection connection;

    public RoleDAO() {
        connection = DatabaseManager
                .getInstance()
                .getConnection();
    }

    public List<Role> findAll() {

        List<Role> roles = new ArrayList<>();

        String sql = """
                SELECT *
                FROM roles
                ORDER BY id_role
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                roles.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roles;
    }

    private Role mapRow(ResultSet rs) throws SQLException {

        Role role = new Role();

        role.setIdRole(rs.getInt("id_role"));
        role.setRoleName(rs.getString("role_name"));

        return role;
    }
}
