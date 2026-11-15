package com.campuscare.dao.jdbc;

import com.campuscare.dao.ComplaintHistoryDao;
import com.campuscare.model.ComplaintHistory;
import com.campuscare.util.DatabaseConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComplaintHistoryJdbcDao extends BaseJdbcDao implements ComplaintHistoryDao {

    @Override
    public Optional<ComplaintHistory> findById(Integer id) {
        String sql = "SELECT history_id, complaint_id, status_from, status_to, remarks, changed_by_name, changed_by_role, timestamp FROM complaint_history WHERE history_id = ?";
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
    public List<ComplaintHistory> findAll() {
        List<ComplaintHistory> list = new ArrayList<>();
        String sql = "SELECT history_id, complaint_id, status_from, status_to, remarks, changed_by_name, changed_by_role, timestamp FROM complaint_history ORDER BY timestamp ASC";
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
    public ComplaintHistory save(ComplaintHistory h) {
        String sql = "INSERT INTO complaint_history (complaint_id, status_from, status_to, remarks, changed_by_name, changed_by_role, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet gk = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, h.getComplaintId());
            ps.setString(2, h.getStatusFrom());
            ps.setString(3, h.getStatusTo());
            ps.setString(4, h.getRemarks());
            ps.setString(5, h.getChangedByName());
            ps.setString(6, h.getChangedByRole());
            ps.setTimestamp(7, toTimestamp(h.getTimestamp()));
            ps.executeUpdate();
            gk = ps.getGeneratedKeys();
            if (gk.next()) {
                h.setHistoryId(gk.getInt(1));
            }
            return h;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeQuietly(gk, ps, conn);
        }
    }

    @Override
    public boolean update(ComplaintHistory entity) {
        return false; // History records are append-only audit trail
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM complaint_history WHERE history_id = ?";
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
    public List<ComplaintHistory> findByComplaintId(String complaintId) {
        List<ComplaintHistory> list = new ArrayList<>();
        String sql = "SELECT history_id, complaint_id, status_from, status_to, remarks, changed_by_name, changed_by_role, timestamp FROM complaint_history WHERE complaint_id = ? ORDER BY timestamp ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, complaintId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs, ps, conn);
        }
        return list;
    }

    private ComplaintHistory mapResultSet(ResultSet rs) throws SQLException {
        return new ComplaintHistory(
                rs.getInt("history_id"),
                rs.getString("complaint_id"),
                rs.getString("status_from"),
                rs.getString("status_to"),
                rs.getString("remarks"),
                rs.getString("changed_by_name"),
                rs.getString("changed_by_role"),
                toLocalDateTime(rs.getTimestamp("timestamp"))
        );
    }
}
