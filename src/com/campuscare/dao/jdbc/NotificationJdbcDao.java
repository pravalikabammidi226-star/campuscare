package com.campuscare.dao.jdbc;

import com.campuscare.dao.NotificationDao;
import com.campuscare.model.Notification;
import com.campuscare.model.enums.UserRole;
import com.campuscare.util.DatabaseConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotificationJdbcDao extends BaseJdbcDao implements NotificationDao {

    @Override
    public Optional<Notification> findById(Integer id) {
        String sql = "SELECT notification_id, recipient_user_id, recipient_role, title, message, is_read, created_at FROM notifications WHERE notification_id = ?";
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
    public List<Notification> findAll() {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT notification_id, recipient_user_id, recipient_role, title, message, is_read, created_at FROM notifications ORDER BY created_at DESC";
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
    public Notification save(Notification n) {
        String sql = "INSERT INTO notifications (recipient_user_id, recipient_role, title, message, is_read, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet gk = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, n.getRecipientUserId());
            ps.setString(2, n.getRecipientRole().name());
            ps.setString(3, n.getTitle());
            ps.setString(4, n.getMessage());
            ps.setBoolean(5, n.isRead());
            ps.setTimestamp(6, toTimestamp(n.getCreatedAt()));
            ps.executeUpdate();
            gk = ps.getGeneratedKeys();
            if (gk.next()) {
                n.setNotificationId(gk.getInt(1));
            }
            return n;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeQuietly(gk, ps, conn);
        }
    }

    @Override
    public boolean update(Notification n) {
        String sql = "UPDATE notifications SET is_read = ? WHERE notification_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, n.isRead());
            ps.setInt(2, n.getNotificationId());
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
        String sql = "DELETE FROM notifications WHERE notification_id = ?";
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
    public List<Notification> findByRecipient(String userId, boolean unreadOnly) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT notification_id, recipient_user_id, recipient_role, title, message, is_read, created_at FROM notifications WHERE recipient_user_id = ? " +
                     (unreadOnly ? "AND is_read = FALSE " : "") + "ORDER BY created_at DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
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

    @Override
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean markAllAsRead(String userId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE recipient_user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(ps, conn);
        }
    }

    private Notification mapResultSet(ResultSet rs) throws SQLException {
        return new Notification(
                rs.getInt("notification_id"),
                rs.getString("recipient_user_id"),
                UserRole.valueOf(rs.getString("recipient_role")),
                rs.getString("title"),
                rs.getString("message"),
                rs.getBoolean("is_read"),
                toLocalDateTime(rs.getTimestamp("created_at"))
        );
    }
}
