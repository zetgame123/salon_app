package ru.ns.dao;

import ru.ns.database.DatabaseManager;
import ru.ns.model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    private final Connection connection;

    public ClientDAO() {
        connection = DatabaseManager
                .getInstance()
                .getConnection();
    }

    public List<Client> findAll() {

        List<Client> clients = new ArrayList<>();

        String sql = """
                SELECT *
                FROM clients
                ORDER BY last_name
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Client client = new Client();

                client.setIdClient(
                        rs.getInt("id_client"));

                client.setFirstName(
                        rs.getString("first_name"));

                client.setLastName(
                        rs.getString("last_name"));

                client.setPhone(
                        rs.getString("phone"));

                client.setRegular(
                        rs.getBoolean("is_regular"));

                clients.add(client);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    public boolean save(Client client) {

        String sql = """
                INSERT INTO clients
                (first_name, last_name, phone, is_regular)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1,
                    client.getFirstName());

            ps.setString(2,
                    client.getLastName());

            ps.setString(3,
                    client.getPhone());

            ps.setBoolean(4,
                    client.isRegular());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(Client client) {

        String sql = """
                UPDATE clients
                SET first_name = ?,
                    last_name = ?,
                    phone = ?,
                    is_regular = ?
                WHERE id_client = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1,
                    client.getFirstName());

            ps.setString(2,
                    client.getLastName());

            ps.setString(3,
                    client.getPhone());

            ps.setBoolean(4,
                    client.isRegular());

            ps.setInt(5,
                    client.getIdClient());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int id) {

        String sql = """
                DELETE FROM clients
                WHERE id_client = ?
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
}