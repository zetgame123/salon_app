package ru.ns.dao;

import ru.ns.database.DatabaseManager;
import ru.ns.model.Branch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {

    private final Connection connection;

    public BranchDAO() {
        connection = DatabaseManager
                .getInstance()
                .getConnection();
    }

    public List<Branch> findAll() {

        List<Branch> branches = new ArrayList<>();

        String sql = """
                SELECT *
                FROM branches
                ORDER BY name
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                branches.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return branches;
    }

    public boolean save(Branch branch) {

        String sql = """
                INSERT INTO branches (name, address)
                VALUES (?, ?)
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, branch.getName());
            ps.setString(2, branch.getAddress());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(Branch branch) {

        String sql = """
                UPDATE branches
                SET name = ?,
                    address = ?
                WHERE id_branch = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, branch.getName());
            ps.setString(2, branch.getAddress());
            ps.setInt(3, branch.getIdBranch());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int id) {

        String sql = """
                DELETE FROM branches
                WHERE id_branch = ?
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

    private Branch mapRow(ResultSet rs) throws SQLException {

        Branch branch = new Branch();

        branch.setIdBranch(rs.getInt("id_branch"));
        branch.setName(rs.getString("name"));
        branch.setAddress(rs.getString("address"));

        return branch;
    }
}
