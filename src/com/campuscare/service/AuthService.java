package com.campuscare.service;

import com.campuscare.dao.AdminDao;
import com.campuscare.dao.CreditWalletDao;
import com.campuscare.dao.DaoFactory;
import com.campuscare.dao.StaffDao;
import com.campuscare.dao.StudentDao;
import com.campuscare.exception.AuthenticationException;
import com.campuscare.exception.ValidationException;
import com.campuscare.model.Admin;
import com.campuscare.model.CreditWallet;
import com.campuscare.model.DepartmentStaff;
import com.campuscare.model.Student;
import com.campuscare.model.User;
import com.campuscare.util.IdGenerator;
import com.campuscare.util.PasswordSecurityUtil;
import com.campuscare.util.ValidationUtil;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service managing user authentication, registration, and credential security.
 */
public class AuthService {

    private final StudentDao studentDao;
    private final AdminDao adminDao;
    private final StaffDao staffDao;
    private final CreditWalletDao walletDao;

    public AuthService() {
        DaoFactory factory = DaoFactory.getInstance();
        this.studentDao = factory.getStudentDao();
        this.adminDao = factory.getAdminDao();
        this.staffDao = factory.getStaffDao();
        this.walletDao = factory.getCreditWalletDao();
    }

    /**
     * Authenticates a student via roll number or email.
     */
    public Student loginStudent(String identifier, String plainPassword) throws AuthenticationException {
        if (identifier == null || plainPassword == null || identifier.trim().isEmpty() || plainPassword.isEmpty()) {
            throw new AuthenticationException("Roll number/Email and password cannot be empty.");
        }
        Optional<Student> studentOpt = studentDao.findByRollNumber(identifier.trim());
        if (!studentOpt.isPresent()) {
            studentOpt = studentDao.findByEmail(identifier.trim());
        }

        if (!studentOpt.isPresent()) {
            throw new AuthenticationException("Student account not found for: " + identifier);
        }

        Student student = studentOpt.get();
        if (!PasswordSecurityUtil.verifyPassword(plainPassword, student.getPasswordHash())) {
            throw new AuthenticationException("Invalid password. Please check your credentials.");
        }

        // Attach wallet
        walletDao.findByStudentId(student.getId()).ifPresent(student::setWallet);
        return student;
    }

    /**
     * Authenticates an administrator via username or email.
     */
    public Admin loginAdmin(String identifier, String plainPassword) throws AuthenticationException {
        if (identifier == null || plainPassword == null || identifier.trim().isEmpty() || plainPassword.isEmpty()) {
            throw new AuthenticationException("Username/Email and password cannot be empty.");
        }
        Optional<Admin> adminOpt = adminDao.findByUsername(identifier.trim());
        if (!adminOpt.isPresent()) {
            adminOpt = adminDao.findByEmail(identifier.trim());
        }

        if (!adminOpt.isPresent()) {
            throw new AuthenticationException("Administrator account not found for: " + identifier);
        }

        Admin admin = adminOpt.get();
        if (!PasswordSecurityUtil.verifyPassword(plainPassword, admin.getPasswordHash())) {
            throw new AuthenticationException("Invalid administrator password.");
        }
        return admin;
    }

    /**
     * Authenticates a department staff member via employee code or email.
     */
    public DepartmentStaff loginStaff(String identifier, String plainPassword) throws AuthenticationException {
        if (identifier == null || plainPassword == null || identifier.trim().isEmpty() || plainPassword.isEmpty()) {
            throw new AuthenticationException("Employee code/Email and password cannot be empty.");
        }
        Optional<DepartmentStaff> staffOpt = staffDao.findByEmployeeCode(identifier.trim());
        if (!staffOpt.isPresent()) {
            staffOpt = staffDao.findByEmail(identifier.trim());
        }

        if (!staffOpt.isPresent()) {
            throw new AuthenticationException("Staff account not found for: " + identifier);
        }

        DepartmentStaff staff = staffOpt.get();
        if (!PasswordSecurityUtil.verifyPassword(plainPassword, staff.getPasswordHash())) {
            throw new AuthenticationException("Invalid staff password.");
        }
        return staff;
    }

    /**
     * Registers a new student and automatically creates their initial Credit Wallet.
     */
    public Student registerStudent(String rollNumber, String name, String email, String plainPassword,
                                   String phone, String department, int semester) throws ValidationException {
        ValidationUtil.validateRollNumber(rollNumber);
        ValidationUtil.validateNotEmpty(name, "Name");
        ValidationUtil.validateEmail(email);
        ValidationUtil.validatePasswordStrength(plainPassword);
        ValidationUtil.validateNotEmpty(department, "Department");
        if (semester < 1 || semester > 8) {
            throw new ValidationException("Semester", "Semester must be between 1 and 8.");
        }

        // Check uniqueness
        if (studentDao.findByRollNumber(rollNumber.trim()).isPresent()) {
            throw new ValidationException("Roll Number", "Student with roll number " + rollNumber + " already exists.");
        }
        if (studentDao.findByEmail(email.trim()).isPresent()) {
            throw new ValidationException("Email", "Student with email " + email + " is already registered.");
        }

        String studentId = IdGenerator.nextStudentId();
        String passwordHash = PasswordSecurityUtil.hashPassword(plainPassword);

        Student student = new Student(studentId, rollNumber.trim().toUpperCase(), name.trim(), email.trim().toLowerCase(),
                passwordHash, phone != null ? phone.trim() : "", department.trim(), semester, LocalDateTime.now());

        studentDao.save(student);

        // Provision initial Credit Wallet
        String walletId = IdGenerator.nextWalletId();
        CreditWallet wallet = new CreditWallet(walletId, studentId, 0, 0, LocalDateTime.now());
        walletDao.save(wallet);
        student.setWallet(wallet);

        return student;
    }
}
