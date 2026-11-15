package com.campuscare.model;

import com.campuscare.model.enums.UserRole;
import java.time.LocalDateTime;

/**
 * Concrete Student entity extending User.
 * Demonstrates Inheritance, Method Overriding, and Constructor Chaining with super().
 */
public class Student extends User {
    private static final long serialVersionUID = 1L;

    private String rollNumber;
    private String department;
    private int semester;
    private CreditWallet wallet;

    public Student() {
        super();
        this.role = UserRole.STUDENT;
    }

    // Overloaded Constructor without wallet
    public Student(String id, String rollNumber, String name, String email, String passwordHash,
                   String phone, String department, int semester) {
        super(id, name, email, passwordHash, phone, UserRole.STUDENT);
        this.rollNumber = rollNumber;
        this.department = department;
        this.semester = semester;
    }

    // Overloaded Constructor with full parameters
    public Student(String id, String rollNumber, String name, String email, String passwordHash,
                   String phone, String department, int semester, LocalDateTime createdAt) {
        super(id, name, email, passwordHash, phone, UserRole.STUDENT, createdAt);
        this.rollNumber = rollNumber;
        this.department = department;
        this.semester = semester;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public CreditWallet getWallet() {
        return wallet;
    }

    public void setWallet(CreditWallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public String getRoleBadge() {
        return "Student (" + rollNumber + " - Sem " + semester + ")";
    }

    @Override
    public String toString() {
        return String.format("Student[Roll: %s, Name: %s, Dept: %s, Sem: %d]",
                rollNumber, name, department, semester);
    }
}
