package ru.ns.dao;

import ru.ns.database.DatabaseManager;
import ru.ns.model.User;

import java.sql.*;
import java.time.LocalDateTime;

public class UserDAO {

    private final Connection connection;

    public UserDAO() {
        connection =
                DatabaseManager
                        .getInstance()
                        .getConnection();
    }

    public User findByLogin(String login) {

        String sql =
                """
                SELECT u.*, r.role_name
                FROM users u
                JOIN roles r
                    ON u.id_role = r.id_role
                WHERE u.login = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, login);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean loginExists(String login) {

        String sql =
                """
                SELECT id_user
                FROM users
                WHERE login = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, login);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean save(User user) {

        String sql =
                """
                INSERT INTO users
                (
                    id_role,
                    login,
                    phone,
                    registration_date,
                    email,
                    password_hash
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, user.getIdRole());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getPhone());
            ps.setTimestamp(
                    4,
                    Timestamp.valueOf(
                            user.getRegistrationDate()));
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPasswordHash());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(User user) {

        String sql =
                """
                UPDATE users
                SET phone = ?,
                    email = ?
                WHERE id_user = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, user.getPhone());
            ps.setString(2, user.getEmail());
            ps.setInt(3, user.getIdUser());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean emailExists(String email) {

        String sql = """
            SELECT id_user
            FROM users
            WHERE email = ?
            """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean emailExistsForOtherUser(
            String email,
            int userId) {

        String sql = """
            SELECT id_user
            FROM users
            WHERE email = ?
              AND id_user <> ?
            """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public User findById(int id) {

        String sql = """
            SELECT u.*, r.role_name
            FROM users u
            JOIN roles r
                ON u.id_role = r.id_role
            WHERE u.id_user = ?
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

    private User mapRow(ResultSet rs) throws SQLException {

        User user = new User();

        user.setIdUser(rs.getInt("id_user"));
        user.setIdRole(rs.getInt("id_role"));
        user.setLogin(rs.getString("login"));
        user.setPhone(rs.getString("phone"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRoleName(rs.getString("role_name"));

        Timestamp timestamp =
                rs.getTimestamp("registration_date");

        if (timestamp != null) {
            user.setRegistrationDate(
                    timestamp.toLocalDateTime());
        }

        return user;
    }
}
