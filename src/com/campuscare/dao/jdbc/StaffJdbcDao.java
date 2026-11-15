package com.campuscare.dao.jdbc;

import com.campuscare.dao.StaffDao;
import com.campuscare.model.DepartmentStaff;
import com.campuscare.util.DatabaseConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StaffJdbcDao extends BaseJdbcDao implements StaffDao {

    @Override
    public Optional<DepartmentStaff> findById(String id) {
        String sql = "SELECT s.staff_id, s.department_id, d.name AS dept_name, s.employee_code, s.name, s.email, s.password_hash, s.phone, s.is_available, s.created_at " +
                     "FROM department_staff s LEFT JOIN departments d ON s.department_id = d.department_id WHERE s.staff_id = ?";
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
    public List<DepartmentStaff> findAll() {
        List<DepartmentStaff> list = new ArrayList<>();
        String sql = "SELECT s.staff_id, s.department_id, d.name AS dept_name, s.employee_code, s.name, s.email, s.password_hash, s.phone, s.is_available, s.created_at " +
                     "FROM department_staff s LEFT JOIN departments d ON s.department_id = d.department_id ORDER BY s.name ASC";
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
    public DepartmentStaff save(DepartmentStaff stf) {
        String sql = "INSERT INTO department_staff (staff_id, department_id, employee_code, name, email, password_hash, phone, is_available, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, stf.getId());
            ps.setInt(2, stf.getDepartmentId());
            ps.setString(3, stf.getEmployeeCode());
            ps.setString(4, stf.getName());
            ps.setString(5, stf.getEmail());
            ps.setString(6, stf.getPasswordHash());
            ps.setString(7, stf.getPhone());
            ps.setBoolean(8, stf.isAvailable());
            ps.setTimestamp(9, toTimestamp(stf.getCreatedAt()));
            ps.executeUpdate();
            return stf;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean update(DepartmentStaff stf) {
        String sql = "UPDATE department_staff SET department_id = ?, name = ?, email = ?, phone = ?, is_available = ? WHERE staff_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, stf.getDepartmentId());
            ps.setString(2, stf.getName());
            ps.setString(3, stf.getEmail());
            ps.setString(4, stf.getPhone());
            ps.setBoolean(5, stf.isAvailable());
            ps.setString(6, stf.getId());
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
        String sql = "DELETE FROM department_staff WHERE staff_id = ?";
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
    public Optional<DepartmentStaff> findByEmployeeCode(String employeeCode) {
        String sql = "SELECT s.staff_id, s.department_id, d.name AS dept_name, s.employee_code, s.name, s.email, s.password_hash, s.phone, s.is_available, s.created_at " +
                     "FROM department_staff s LEFT JOIN departments d ON s.department_id = d.department_id WHERE s.employee_code = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, employeeCode);
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
    public Optional<DepartmentStaff> findByEmail(String email) {
        String sql = "SELECT s.staff_id, s.department_id, d.name AS dept_name, s.employee_code, s.name, s.email, s.password_hash, s.phone, s.is_available, s.created_at " +
                     "FROM department_staff s LEFT JOIN departments d ON s.department_id = d.department_id WHERE s.email = ?";
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

    @Override
    public List<DepartmentStaff> findByDepartmentId(int departmentId) {
        List<DepartmentStaff> list = new ArrayList<>();
        String sql = "SELECT s.staff_id, s.department_id, d.name AS dept_name, s.employee_code, s.name, s.email, s.password_hash, s.phone, s.is_available, s.created_at " +
                     "FROM department_staff s LEFT JOIN departments d ON s.department_id = d.department_id WHERE s.department_id = ? ORDER BY s.name ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, departmentId);
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

    private DepartmentStaff mapResultSet(ResultSet rs) throws SQLException {
        DepartmentStaff stf = new DepartmentStaff(
                rs.getString("staff_id"),
                rs.getInt("department_id"),
                rs.getString("employee_code"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone"),
                rs.getBoolean("is_available"),
                toLocalDateTime(rs.getTimestamp("created_at"))
        );
        stf.setDepartmentName(rs.getString("dept_name"));
        return stf;
    }
}
