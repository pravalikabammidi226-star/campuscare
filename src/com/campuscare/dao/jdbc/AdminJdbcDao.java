package com.campuscare.dao.jdbc;

import com.campuscare.dao.AdminDao;
import com.campuscare.model.Admin;
import com.campuscare.util.DatabaseConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminJdbcDao extends BaseJdbcDao implements AdminDao {

    @Override
    public Optional<Admin> findById(String id) {
        String sql = "SELECT admin_id, username, password_hash, full_name, email, role_title, created_at FROM admins WHERE admin_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
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
    public List<Admin> findAll() {
        List<Admin> list = new ArrayList<>();
        String sql = "SELECT admin_id, username, password_hash, full_name, email, role_title, created_at FROM admins";
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
    public Admin save(Admin a) {
        String sql = "INSERT INTO admins (admin_id, username, password_hash, full_name, email, role_title, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, a.getId());
            ps.setString(2, a.getUsername());
            ps.setString(3, a.getPasswordHash());
            ps.setString(4, a.getName());
            ps.setString(5, a.getEmail());
            ps.setString(6, a.getRoleTitle());
            ps.setTimestamp(7, toTimestamp(a.getCreatedAt()));
            ps.executeUpdate();
            return a;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean update(Admin a) {
        String sql = "UPDATE admins SET full_name = ?, email = ?, role_title = ? WHERE admin_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, a.getName());
            ps.setString(2, a.getEmail());
            ps.setString(3, a.getRoleTitle());
            ps.setString(4, a.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean deleteById(String id) {
        String sql = "DELETE FROM admins WHERE admin_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(ps, conn);
        }
    }

    @Override
    public Optional<Admin> findByUsername(String username) {
        String sql = "SELECT admin_id, username, password_hash, full_name, email, role_title, created_at FROM admins WHERE username = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
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
    public Optional<Admin> findByEmail(String email) {
        String sql = "SELECT admin_id, username, password_hash, full_name, email, role_title, created_at FROM admins WHERE email = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
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

    private Admin mapResultSet(ResultSet rs) throws SQLException {
        return new Admin(
                rs.getString("admin_id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("role_title"),
                toLocalDateTime(rs.getTimestamp("created_at"))
        );
    }
}
