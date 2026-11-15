package com.campuscare.dao.jdbc;

import com.campuscare.dao.FeedbackDao;
import com.campuscare.model.Feedback;
import com.campuscare.util.DatabaseConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FeedbackJdbcDao extends BaseJdbcDao implements FeedbackDao {

    @Override
    public Optional<Feedback> findById(Integer id) {
        String sql = "SELECT feedback_id, complaint_id, student_id, rating, comments, created_at FROM feedback WHERE feedback_id = ?";
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
    public List<Feedback> findAll() {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT feedback_id, complaint_id, student_id, rating, comments, created_at FROM feedback ORDER BY created_at DESC";
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
    public Feedback save(Feedback f) {
        String sql = "INSERT INTO feedback (complaint_id, student_id, rating, comments, created_at) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet gk = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, f.getComplaintId());
            ps.setString(2, f.getStudentId());
            ps.setInt(3, f.getRating());
            ps.setString(4, f.getComments());
            ps.setTimestamp(5, toTimestamp(f.getCreatedAt()));
            ps.executeUpdate();
            gk = ps.getGeneratedKeys();
            if (gk.next()) {
                f.setFeedbackId(gk.getInt(1));
            }
            return f;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeQuietly(gk, ps, conn);
        }
    }

    @Override
    public boolean update(Feedback f) {
        String sql = "UPDATE feedback SET rating = ?, comments = ? WHERE feedback_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, f.getRating());
            ps.setString(2, f.getComments());
            ps.setInt(3, f.getFeedbackId());
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
        String sql = "DELETE FROM feedback WHERE feedback_id = ?";
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
    public Optional<Feedback> findByComplaintId(String complaintId) {
        String sql = "SELECT feedback_id, complaint_id, student_id, rating, comments, created_at FROM feedback WHERE complaint_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, complaintId);
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
    public List<Feedback> findByStudentId(String studentId) {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT feedback_id, complaint_id, student_id, rating, comments, created_at FROM feedback WHERE student_id = ? ORDER BY created_at DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, studentId);
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

    private Feedback mapResultSet(ResultSet rs) throws SQLException {
        return new Feedback(
                rs.getInt("feedback_id"),
                rs.getString("complaint_id"),
                rs.getString("student_id"),
                rs.getInt("rating"),
                rs.getString("comments"),
                toLocalDateTime(rs.getTimestamp("created_at"))
        );
    }
}
