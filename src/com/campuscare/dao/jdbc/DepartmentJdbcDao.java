package com.campuscare.dao.jdbc;

import com.campuscare.dao.DepartmentDao;
import com.campuscare.model.Department;
import com.campuscare.util.DatabaseConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentJdbcDao extends BaseJdbcDao implements DepartmentDao {

    @Override
    public Optional<Department> findById(Integer id) {
        String sql = "SELECT department_id, name, code, contact_email, description, created_at FROM departments WHERE department_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs, ps, conn);
        }
        return Optional.empty();
    }

    @Override
    public List<Department> findAll() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT department_id, name, code, contact_email, description, created_at FROM departments ORDER BY department_id ASC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs, stmt, conn);
        }
        return list;
    }

    @Override
    public Department save(Department d) {
        String sql = "INSERT INTO departments (name, code, contact_email, description, created_at) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, d.getName());
            ps.setString(2, d.getCode());
            ps.setString(3, d.getContactEmail());
            ps.setString(4, d.getDescription());
            ps.setTimestamp(5, toTimestamp(d.getCreatedAt()));
            ps.executeUpdate();
            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                d.setDepartmentId(generatedKeys.getInt(1));
            }
            return d;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeQuietly(generatedKeys, ps, conn);
        }
    }

    @Override
    public boolean update(Department d) {
        String sql = "UPDATE departments SET name = ?, code = ?, contact_email = ?, description = ? WHERE department_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, d.getName());
            ps.setString(2, d.getCode());
            ps.setString(3, d.getContactEmail());
            ps.setString(4, d.getDescription());
            ps.setInt(5, d.getDepartmentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM departments WHERE department_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(ps, conn);
        }
    }

    @Override
    public Optional<Department> findByCode(String code) {
        String sql = "SELECT department_id, name, code, contact_email, description, created_at FROM departments WHERE code = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, code);
            rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs, ps, conn);
        }
        return Optional.empty();
    }

    private Department mapResultSet(ResultSet rs) throws SQLException {
        Department d = new Department(
                rs.getInt("department_id"),
                rs.getString("name"),
                rs.getString("code"),
                rs.getString("contact_email"),
                rs.getString("description")
        );
        d.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
        return d;
    }
}
