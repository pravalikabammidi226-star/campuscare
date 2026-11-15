package com.campuscare.dao.jdbc;

import com.campuscare.dao.ComplaintDao;
import com.campuscare.model.Complaint;
import com.campuscare.model.enums.ComplaintCategory;
import com.campuscare.model.enums.ComplaintStatus;
import com.campuscare.model.enums.PriorityLevel;
import com.campuscare.util.DatabaseConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComplaintJdbcDao extends BaseJdbcDao implements ComplaintDao {

    private static final String BASE_SELECT =
            "SELECT c.complaint_id, c.ticket_number, c.student_id, s.name AS student_name, s.roll_number AS student_roll, " +
            "c.category, c.title, c.description, c.location, c.priority, c.status, c.is_anonymous, " +
            "c.assigned_department_id, d.name AS dept_name, c.assigned_staff_id, stf.name AS staff_name, " +
            "c.created_at, c.deadline_at, c.resolved_at, c.rejection_reason " +
            "FROM complaints c " +
            "LEFT JOIN students s ON c.student_id = s.student_id " +
            "LEFT JOIN departments d ON c.assigned_department_id = d.department_id " +
            "LEFT JOIN department_staff stf ON c.assigned_staff_id = stf.staff_id ";

    @Override
    public Optional<Complaint> findById(String id) {
        String sql = BASE_SELECT + "WHERE c.complaint_id = ?";
        return querySingle(sql, id);
    }

    @Override
    public Optional<Complaint> findByTicketNumber(String ticketNumber) {
        String sql = BASE_SELECT + "WHERE c.ticket_number = ?";
        return querySingle(sql, ticketNumber);
    }

    @Override
    public List<Complaint> findAll() {
        String sql = BASE_SELECT + "ORDER BY c.created_at DESC";
        return queryList(sql);
    }

    @Override
    public List<Complaint> findByStudentId(String studentId) {
        String sql = BASE_SELECT + "WHERE c.student_id = ? ORDER BY c.created_at DESC";
        return queryList(sql, studentId);
    }

    @Override
    public List<Complaint> findByDepartmentId(int departmentId) {
        String sql = BASE_SELECT + "WHERE c.assigned_department_id = ? ORDER BY c.created_at DESC";
        return queryList(sql, departmentId);
    }

    @Override
    public List<Complaint> findByStaffId(String staffId) {
        String sql = BASE_SELECT + "WHERE c.assigned_staff_id = ? ORDER BY c.created_at DESC";
        return queryList(sql, staffId);
    }

    @Override
    public List<Complaint> findByStatus(ComplaintStatus status) {
        String sql = BASE_SELECT + "WHERE c.status = ? ORDER BY c.created_at DESC";
        return queryList(sql, status.name());
    }

    @Override
    public List<Complaint> findByCategory(ComplaintCategory category) {
        String sql = BASE_SELECT + "WHERE c.category = ? ORDER BY c.created_at DESC";
        return queryList(sql, category.name());
    }

    @Override
    public List<Complaint> findActiveComplaints() {
        String sql = BASE_SELECT + "WHERE c.status NOT IN ('RESOLVED', 'REJECTED', 'CLOSED') ORDER BY c.created_at DESC";
        return queryList(sql);
    }

    @Override
    public List<Complaint> findOverdueComplaints() {
        String sql = BASE_SELECT + "WHERE c.status NOT IN ('RESOLVED', 'REJECTED', 'CLOSED') AND c.deadline_at < NOW() ORDER BY c.deadline_at ASC";
        return queryList(sql);
    }

    @Override
    public Complaint save(Complaint c) {
        String sql = "INSERT INTO complaints (complaint_id, ticket_number, student_id, category, title, description, location, priority, status, is_anonymous, assigned_department_id, assigned_staff_id, created_at, deadline_at, resolved_at, rejection_reason) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, c.getComplaintId());
            ps.setString(2, c.getTicketNumber());
            ps.setString(3, c.getStudentId());
            ps.setString(4, c.getCategory().name());
            ps.setString(5, c.getTitle());
            ps.setString(6, c.getDescription());
            ps.setString(7, c.getLocation());
            ps.setString(8, c.getPriority().name());
            ps.setString(9, c.getStatus().name());
            ps.setBoolean(10, c.isAnonymous());
            if (c.getAssignedDepartmentId() != null) ps.setInt(11, c.getAssignedDepartmentId());
            else ps.setNull(11, Types.INTEGER);
            if (c.getAssignedStaffId() != null) ps.setString(12, c.getAssignedStaffId());
            else ps.setNull(12, Types.VARCHAR);
            ps.setTimestamp(13, toTimestamp(c.getCreatedAt()));
            ps.setTimestamp(14, toTimestamp(c.getDeadlineAt()));
            ps.setTimestamp(15, toTimestamp(c.getResolvedAt()));
            ps.setString(16, c.getRejectionReason());
            ps.executeUpdate();
            return c;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean update(Complaint c) {
        String sql = "UPDATE complaints SET category = ?, title = ?, description = ?, location = ?, priority = ?, status = ?, is_anonymous = ?, assigned_department_id = ?, assigned_staff_id = ?, deadline_at = ?, resolved_at = ?, rejection_reason = ? WHERE complaint_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, c.getCategory().name());
            ps.setString(2, c.getTitle());
            ps.setString(3, c.getDescription());
            ps.setString(4, c.getLocation());
            ps.setString(5, c.getPriority().name());
            ps.setString(6, c.getStatus().name());
            ps.setBoolean(7, c.isAnonymous());
            if (c.getAssignedDepartmentId() != null) ps.setInt(8, c.getAssignedDepartmentId());
            else ps.setNull(8, Types.INTEGER);
            if (c.getAssignedStaffId() != null) ps.setString(9, c.getAssignedStaffId());
            else ps.setNull(9, Types.VARCHAR);
            ps.setTimestamp(10, toTimestamp(c.getDeadlineAt()));
            ps.setTimestamp(11, toTimestamp(c.getResolvedAt()));
            ps.setString(12, c.getRejectionReason());
            ps.setString(13, c.getComplaintId());
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
        String sql = "DELETE FROM complaints WHERE complaint_id = ?";
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

    private Optional<Complaint> querySingle(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
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

    private List<Complaint> queryList(String sql, Object... params) {
        List<Complaint> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
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

    private Complaint mapResultSet(ResultSet rs) throws SQLException {
        Integer deptId = rs.getObject("assigned_department_id") != null ? rs.getInt("assigned_department_id") : null;
        return new Complaint(
                rs.getString("complaint_id"),
                rs.getString("ticket_number"),
                rs.getString("student_id"),
                rs.getString("student_name"),
                rs.getString("student_roll"),
                ComplaintCategory.valueOf(rs.getString("category")),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("location"),
                PriorityLevel.valueOf(rs.getString("priority")),
                ComplaintStatus.valueOf(rs.getString("status")),
                rs.getBoolean("is_anonymous"),
                deptId,
                rs.getString("dept_name"),
                rs.getString("assigned_staff_id"),
                rs.getString("staff_name"),
                toLocalDateTime(rs.getTimestamp("created_at")),
                toLocalDateTime(rs.getTimestamp("deadline_at")),
                toLocalDateTime(rs.getTimestamp("resolved_at")),
                rs.getString("rejection_reason")
        );
    }
}
