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
                ORDER BY surname, name
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                clients.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    public boolean save(Client client) {

        String sql = """
                INSERT INTO clients
                (surname, name, patronymic, phone, regular_client)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, client.getSurname());
            ps.setString(2, client.getName());
            ps.setString(3, client.getPatronymic());
            ps.setString(4, client.getPhone());
            ps.setBoolean(5, client.isRegularClient());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(Client client) {

        String sql = """
                UPDATE clients
                SET surname = ?,
                    name = ?,
                    patronymic = ?,
                    phone = ?,
                    regular_client = ?
                WHERE id_client = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, client.getSurname());
            ps.setString(2, client.getName());
            ps.setString(3, client.getPatronymic());
            ps.setString(4, client.getPhone());
            ps.setBoolean(5, client.isRegularClient());
            ps.setInt(6, client.getIdClient());

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

    private Client mapRow(ResultSet rs) throws SQLException {

        Client client = new Client();

        client.setIdClient(rs.getInt("id_client"));
        client.setSurname(rs.getString("surname"));
        client.setName(rs.getString("name"));
        client.setPatronymic(rs.getString("patronymic"));
        client.setPhone(rs.getString("phone"));
        client.setRegularClient(rs.getBoolean("regular_client"));

        return client;
    }
}
