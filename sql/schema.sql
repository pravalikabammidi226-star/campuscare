-- ============================================================================
-- CampusCare+ – Smart Campus Complaint & Grievance Management System
-- Database Schema Definition (MySQL 8.x)
-- ============================================================================

DROP DATABASE IF EXISTS campuscare_db;
CREATE DATABASE campuscare_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campuscare_db;

-- ----------------------------------------------------------------------------
-- Table 1: Departments
-- ----------------------------------------------------------------------------
CREATE TABLE departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    contact_email VARCHAR(120) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Table 2: Students
-- ----------------------------------------------------------------------------
CREATE TABLE students (
    student_id VARCHAR(36) PRIMARY KEY,
    roll_number VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    department VARCHAR(80) NOT NULL,
    semester INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_student_roll (roll_number),
    INDEX idx_student_email (email)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Table 3: Admins
-- ----------------------------------------------------------------------------
CREATE TABLE admins (
    admin_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    role_title VARCHAR(50) DEFAULT 'Campus Administrator',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Table 4: DepartmentStaff
-- ----------------------------------------------------------------------------
CREATE TABLE department_staff (
    staff_id VARCHAR(36) PRIMARY KEY,
    department_id INT NOT NULL,
    employee_code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    phone VARCHAR(20),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE RESTRICT,
    INDEX idx_staff_dept (department_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Table 5: Complaints
-- ----------------------------------------------------------------------------
CREATE TABLE complaints (
    complaint_id VARCHAR(36) PRIMARY KEY,
    ticket_number VARCHAR(20) NOT NULL UNIQUE,
    student_id VARCHAR(36) NOT NULL,
    category ENUM('CLASSROOM_LAB', 'HOSTEL', 'LIBRARY', 'CANTEEN_HYGIENE', 'TRANSPORTATION', 'WIFI_TECHNICAL', 'WATER_ELECTRICITY', 'RAGGING_HARASSMENT', 'OTHER') NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(120) NOT NULL,
    priority ENUM('NORMAL', 'HIGH', 'URGENT') NOT NULL DEFAULT 'NORMAL',
    status ENUM('SUBMITTED', 'UNDER_REVIEW', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'REJECTED', 'ESCALATED', 'CLOSED') NOT NULL DEFAULT 'SUBMITTED',
    is_anonymous BOOLEAN DEFAULT FALSE,
    assigned_department_id INT NULL,
    assigned_staff_id VARCHAR(36) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deadline_at TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP NULL,
    rejection_reason TEXT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_department_id) REFERENCES departments(department_id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_staff_id) REFERENCES department_staff(staff_id) ON DELETE SET NULL,
    INDEX idx_complaint_status (status),
    INDEX idx_complaint_priority (priority),
    INDEX idx_complaint_cat (category),
    INDEX idx_complaint_loc (location),
    INDEX idx_complaint_deadline (deadline_at)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Table 6: ComplaintHistory
-- ----------------------------------------------------------------------------
CREATE TABLE complaint_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id VARCHAR(36) NOT NULL,
    status_from VARCHAR(30),
    status_to VARCHAR(30) NOT NULL,
    remarks TEXT NOT NULL,
    changed_by_name VARCHAR(100) NOT NULL,
    changed_by_role VARCHAR(30) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id) ON DELETE CASCADE,
    INDEX idx_history_complaint (complaint_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Table 7: Feedback
-- ----------------------------------------------------------------------------
CREATE TABLE feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id VARCHAR(36) NOT NULL UNIQUE,
    student_id VARCHAR(36) NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Table 8: CreditWallets
-- ----------------------------------------------------------------------------
CREATE TABLE credit_wallets (
    wallet_id VARCHAR(36) PRIMARY KEY,
    student_id VARCHAR(36) NOT NULL UNIQUE,
    total_balance INT NOT NULL DEFAULT 0 CHECK (total_balance >= 0),
    lifetime_earned INT NOT NULL DEFAULT 0 CHECK (lifetime_earned >= 0),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Table 9: CreditTransactions
-- ----------------------------------------------------------------------------
CREATE TABLE credit_transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    wallet_id VARCHAR(36) NOT NULL,
    complaint_id VARCHAR(36) NULL,
    transaction_type ENUM('COMPLAINT_SUBMISSION', 'SUPPLEMENTARY_INFO', 'RESOLUTION_FEEDBACK', 'VERIFIED_SAFETY_BONUS', 'VERIFICATION_HELP', 'MANUAL_ADJUSTMENT') NOT NULL,
    credits INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES credit_wallets(wallet_id) ON DELETE CASCADE,
    FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id) ON DELETE SET NULL,
    INDEX idx_trans_wallet (wallet_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Table 10: Notifications
-- ----------------------------------------------------------------------------
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    recipient_user_id VARCHAR(36) NOT NULL,
    recipient_role ENUM('STUDENT', 'ADMIN', 'STAFF') NOT NULL,
    title VARCHAR(120) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notif_recipient (recipient_user_id, is_read)
) ENGINE=InnoDB;
