-- ============================================================================
-- CampusCare+ – Realistic Seed Data Script
-- Populates departments, initial students, admins, staff, complaints,
-- credit wallets, transactions, and notifications.
-- ============================================================================

USE campuscare_db;

-- ----------------------------------------------------------------------------
-- 1. Departments
-- ----------------------------------------------------------------------------
INSERT INTO departments (department_id, name, code, contact_email, description) VALUES
(1, 'Hostel & Residential Life', 'HOSTEL', 'hostel.admin@campus.edu', 'Manages student accommodation, room maintenance, and hostel facilities.'),
(2, 'Electrical & Power Systems', 'ELEC', 'electrical.maint@campus.edu', 'Handles campus power supply, lighting, wiring, and electrical infrastructure.'),
(3, 'IT Infrastructure & Wi-Fi', 'IT_INFRA', 'ithelpdesk@campus.edu', 'Responsible for campus network, Wi-Fi access points, and computer labs.'),
(4, 'Estate & Water Works', 'ESTATE', 'estate.water@campus.edu', 'Oversees plumbing, water purifiers, sanitation, and building repairs.'),
(5, 'Food & Canteen Committee', 'CANTEEN', 'canteen.hygiene@campus.edu', 'Monitors cafeteria hygiene, food quality, pricing, and cleanliness.'),
(6, 'Campus Safety & Anti-Ragging Cell', 'SAFETY', 'antiragging.cell@campus.edu', 'Confidential unit handling campus security, harassment, and ragging complaints.');

-- ----------------------------------------------------------------------------
-- 2. Administrator
-- Default Admin: username = admin, password = admin123 (SHA-256)
-- ----------------------------------------------------------------------------
INSERT INTO admins (admin_id, username, password_hash, full_name, email, role_title) VALUES
('ADM-001', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Prof. Arvind Sharma', 'arvind.sharma@campus.edu', 'Chief Grievance Officer');

-- ----------------------------------------------------------------------------
-- 3. Department Staff
-- Default Staff: password = staff123 (SHA-256)
-- ----------------------------------------------------------------------------
INSERT INTO department_staff (staff_id, department_id, employee_code, name, email, password_hash, phone, is_available) VALUES
('STF-001', 1, 'EMP-HOSTEL-01', 'Ramesh Kumar', 'ramesh.hostel@campus.edu', 'cb047cf8f4201be9c3b8794c4897f267a149b56f8f0abec5f52479e0f39ac250', '+91 9876543210', TRUE),
('STF-002', 2, 'EMP-ELEC-01', 'Suresh Patel', 'suresh.elec@campus.edu', 'cb047cf8f4201be9c3b8794c4897f267a149b56f8f0abec5f52479e0f39ac250', '+91 9876543211', TRUE),
('STF-003', 3, 'EMP-IT-01', 'Pooja Verma', 'pooja.it@campus.edu', 'cb047cf8f4201be9c3b8794c4897f267a149b56f8f0abec5f52479e0f39ac250', '+91 9876543212', TRUE),
('STF-004', 4, 'EMP-ESTATE-01', 'Mahesh Yadav', 'mahesh.estate@campus.edu', 'cb047cf8f4201be9c3b8794c4897f267a149b56f8f0abec5f52479e0f39ac250', '+91 9876543213', TRUE),
('STF-005', 5, 'EMP-CANTEEN-01', 'Anita Desai', 'anita.canteen@campus.edu', 'cb047cf8f4201be9c3b8794c4897f267a149b56f8f0abec5f52479e0f39ac250', '+91 9876543214', TRUE),
('STF-006', 6, 'EMP-SAFETY-01', 'Dr. Sunita Rao', 'sunita.safety@campus.edu', 'cb047cf8f4201be9c3b8794c4897f267a149b56f8f0abec5f52479e0f39ac250', '+91 9876543215', TRUE);

-- ----------------------------------------------------------------------------
-- 4. Students
-- Default Student: password = password123 (SHA-256)
-- ----------------------------------------------------------------------------
INSERT INTO students (student_id, roll_number, name, email, password_hash, phone, department, semester) VALUES
('STU-001', '22CS0101', 'Aarav Mehta', 'aarav.mehta@student.campus.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', '+91 9123456780', 'Computer Science & Engineering', 5),
('STU-002', '22EC0204', 'Diya Nair', 'diya.nair@student.campus.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', '+91 9123456781', 'Electronics & Communication', 5),
('STU-003', '23ME0305', 'Rohan Gupta', 'rohan.gupta@student.campus.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', '+91 9123456782', 'Mechanical Engineering', 3);

-- ----------------------------------------------------------------------------
-- 5. Credit Wallets
-- ----------------------------------------------------------------------------
INSERT INTO credit_wallets (wallet_id, student_id, total_balance, lifetime_earned) VALUES
('WLT-001', 'STU-001', 15, 15),
('WLT-002', 'STU-002', 10, 10),
('WLT-003', 'STU-003', 5, 5);

-- ----------------------------------------------------------------------------
-- 6. Credit Transactions
-- ----------------------------------------------------------------------------
INSERT INTO credit_transactions (wallet_id, transaction_type, credits, description) VALUES
('WLT-001', 'COMPLAINT_SUBMISSION', 5, 'Initial submission reward for valid ticket CMP-2026-001'),
('WLT-001', 'VERIFIED_SAFETY_BONUS', 5, 'Verified electrical safety hazard bonus CMP-2026-001'),
('WLT-001', 'RESOLUTION_FEEDBACK', 2, 'Feedback provided for resolved ticket CMP-2026-001'),
('WLT-001', 'VERIFICATION_HELP', 3, 'Assisted department in verifying electrical repair'),
('WLT-002', 'COMPLAINT_SUBMISSION', 5, 'Valid complaint submission for CMP-2026-002'),
('WLT-002', 'SUPPLEMENTARY_INFO', 3, 'Uploaded detailed photos and room location'),
('WLT-002', 'RESOLUTION_FEEDBACK', 2, 'Feedback provided for CMP-2026-002'),
('WLT-003', 'COMPLAINT_SUBMISSION', 5, 'Valid complaint submission for CMP-2026-003');

-- ----------------------------------------------------------------------------
-- 7. Complaints
-- ----------------------------------------------------------------------------
INSERT INTO complaints (complaint_id, ticket_number, student_id, category, title, description, location, priority, status, is_anonymous, assigned_department_id, assigned_staff_id, created_at, deadline_at, resolved_at) VALUES
('CMP-001', 'CMP-2026-001', 'STU-001', 'WATER_ELECTRICITY', 'Exposed High Voltage Wire near Lab 304', 'Exposed electrical wires hanging near the entrance of Computer Lab 304, sparking occasionally.', 'Academic Block B - 3rd Floor', 'URGENT', 'RESOLVED', FALSE, 2, 'STF-002', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('CMP-002', 'CMP-2026-002', 'STU-002', 'HOSTEL', 'Water Leakage in Hostel Block C Washroom', 'Continuous pipe leakage in 2nd floor bathroom causing water accumulation and slipping hazard.', 'Hostel Block C - Floor 2', 'HIGH', 'IN_PROGRESS', FALSE, 1, 'STF-001', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY), NULL),
('CMP-003', 'CMP-2026-003', 'STU-003', 'WIFI_TECHNICAL', 'Wi-Fi Signal Dropping in Central Library', 'Signal keeps disconnecting every 5 minutes in the digital reading section.', 'Central Library - 1st Floor', 'NORMAL', 'SUBMITTED', FALSE, NULL, NULL, DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_ADD(NOW(), INTERVAL 6 DAY), NULL),
('CMP-004', 'CMP-2026-004', 'STU-001', 'RAGGING_HARASSMENT', 'Intimidation near North Gate parking after 8 PM', 'Senior students gathering near dark section of North Gate demanding personal belongings.', 'North Gate Parking Area', 'URGENT', 'UNDER_REVIEW', TRUE, 6, 'STF-006', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 12 HOUR), NULL);

-- ----------------------------------------------------------------------------
-- 8. Complaint History
-- ----------------------------------------------------------------------------
INSERT INTO complaint_history (complaint_id, status_from, status_to, remarks, changed_by_name, changed_by_role) VALUES
('CMP-001', 'SUBMITTED', 'ASSIGNED', 'Assigned with high urgency to Electrical Maintenance department.', 'Prof. Arvind Sharma', 'ADMIN'),
('CMP-001', 'ASSIGNED', 'IN_PROGRESS', 'Inspection started; main switch breaker isolated.', 'Suresh Patel', 'STAFF'),
('CMP-001', 'IN_PROGRESS', 'RESOLVED', 'Insulated conduit installed, replaced burnt socket and retested.', 'Suresh Patel', 'STAFF'),
('CMP-002', 'SUBMITTED', 'ASSIGNED', 'Forwarded to Hostel Estate team.', 'Prof. Arvind Sharma', 'ADMIN'),
('CMP-002', 'ASSIGNED', 'IN_PROGRESS', 'Plumber dispatched with replacement PVC joints.', 'Ramesh Kumar', 'STAFF');

-- ----------------------------------------------------------------------------
-- 9. Feedback
-- ----------------------------------------------------------------------------
INSERT INTO feedback (complaint_id, student_id, rating, comments) VALUES
('CMP-001', 'STU-001', 5, 'Prompt resolution within 24 hours. Very grateful for rapid electrical hazard fix!');

-- ----------------------------------------------------------------------------
-- 10. Notifications
-- ----------------------------------------------------------------------------
INSERT INTO notifications (recipient_user_id, recipient_role, title, message, is_read) VALUES
('STU-001', 'STUDENT', 'Complaint Resolved (CMP-2026-001)', 'Your complaint regarding exposed wire near Lab 304 has been resolved. You earned +2 feedback credits!', TRUE),
('STU-002', 'STUDENT', 'Work in Progress (CMP-2026-002)', 'Hostel staff Ramesh Kumar has commenced work on your washroom leakage complaint.', FALSE),
('STF-002', 'STAFF', 'Urgent Ticket Assigned', 'Urgent complaint CMP-2026-001 assigned to you.', TRUE),
('ADM-001', 'ADMIN', 'New Sensitive Complaint Filed', 'Confidential complaint CMP-2026-004 filed under Ragging & Harassment category.', FALSE);
