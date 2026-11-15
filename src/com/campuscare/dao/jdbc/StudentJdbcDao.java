package com.campuscare.dao.jdbc;

import com.campuscare.dao.StudentDao;
import com.campuscare.model.Student;
import com.campuscare.util.DatabaseConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of StudentDao utilizing PreparedStatement and transaction safety.
 */
public class StudentJdbcDao extends BaseJdbcDao implements StudentDao {

    @Override
    public Optional<Student> findById(String id) {
        String sql = "SELECT student_id, roll_number, name, email, password_hash, phone, department, semester, created_at FROM students WHERE student_id = ?";
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
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT student_id, roll_number, name, email, password_hash, phone, department, semester, created_at FROM students ORDER BY roll_number ASC";
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
    public Student save(Student s) {
        String sql = "INSERT INTO students (student_id, roll_number, name, email, password_hash, phone, department, semester, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, s.getId());
            ps.setString(2, s.getRollNumber());
            ps.setString(3, s.getName());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPasswordHash());
            ps.setString(6, s.getPhone());
            ps.setString(7, s.getDepartment());
            ps.setInt(8, s.getSemester());
            ps.setTimestamp(9, toTimestamp(s.getCreatedAt()));
            ps.executeUpdate();
            return s;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean update(Student s) {
        String sql = "UPDATE students SET name = ?, email = ?, phone = ?, department = ?, semester = ? WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, s.getName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPhone());
            ps.setString(4, s.getDepartment());
            ps.setInt(5, s.getSemester());
            ps.setString(6, s.getId());
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
        String sql = "DELETE FROM students WHERE student_id = ?";
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
    public Optional<Student> findByRollNumber(String rollNumber) {
        String sql = "SELECT student_id, roll_number, name, email, password_hash, phone, department, semester, created_at FROM students WHERE roll_number = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, rollNumber);
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
    public Optional<Student> findByEmail(String email) {
        String sql = "SELECT student_id, roll_number, name, email, password_hash, phone, department, semester, created_at FROM students WHERE email = ?";
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

    private Student mapResultSet(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("student_id"),
                rs.getString("roll_number"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone"),
                rs.getString("department"),
                rs.getInt("semester"),
                toLocalDateTime(rs.getTimestamp("created_at"))
        );
    }
}
