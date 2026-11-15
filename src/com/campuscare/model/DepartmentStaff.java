package com.campuscare.model;

import com.campuscare.model.enums.UserRole;
import java.time.LocalDateTime;

/**
 * Concrete DepartmentStaff entity extending User.
 * Represents operational personnel assigned to resolve technical, hostel, or maintenance complaints.
 */
public class DepartmentStaff extends User {
    private static final long serialVersionUID = 1L;

    private int departmentId;
    private String departmentName;
    private String employeeCode;
    private boolean available;

    public DepartmentStaff() {
        super();
        this.role = UserRole.STAFF;
        this.available = true;
    }

    public DepartmentStaff(String id, int departmentId, String employeeCode, String name,
                           String email, String passwordHash, String phone) {
        super(id, name, email, passwordHash, phone, UserRole.STAFF);
        this.departmentId = departmentId;
        this.employeeCode = employeeCode;
        this.available = true;
    }

    public DepartmentStaff(String id, int departmentId, String employeeCode, String name,
                           String email, String passwordHash, String phone, boolean available, LocalDateTime createdAt) {
        super(id, name, email, passwordHash, phone, UserRole.STAFF, createdAt);
        this.departmentId = departmentId;
        this.employeeCode = employeeCode;
        this.available = available;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String getRoleBadge() {
        return "Staff: " + (departmentName != null ? departmentName : "Dept " + departmentId) + " (" + employeeCode + ")";
    }

    @Override
    public String toString() {
        return String.format("DepartmentStaff[Code: %s, Name: %s, Dept: %s]",
                employeeCode, name, departmentName != null ? departmentName : departmentId);
    }
}
