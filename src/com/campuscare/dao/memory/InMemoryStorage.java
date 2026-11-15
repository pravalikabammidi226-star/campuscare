package com.campuscare.dao.memory;

import com.campuscare.model.*;
import com.campuscare.model.enums.*;
import com.campuscare.util.PasswordSecurityUtil;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe In-Memory persistent store pre-loaded with realistic campus seed data.
 * Demonstrates Collections Framework (ConcurrentHashMap, ArrayList), Atomic counters,
 * and seamless fallback capability for offline evaluations.
 */
public class InMemoryStorage {

    private static final InMemoryStorage INSTANCE = new InMemoryStorage();

    public final Map<String, Student> students = new ConcurrentHashMap<>();
    public final Map<String, Admin> admins = new ConcurrentHashMap<>();
    public final Map<String, DepartmentStaff> staff = new ConcurrentHashMap<>();
    public final Map<Integer, Department> departments = new ConcurrentHashMap<>();
    public final Map<String, Complaint> complaints = new ConcurrentHashMap<>();
    public final Map<Integer, ComplaintHistory> complaintHistories = new ConcurrentHashMap<>();
    public final Map<Integer, Feedback> feedbacks = new ConcurrentHashMap<>();
    public final Map<String, CreditWallet> wallets = new ConcurrentHashMap<>();
    public final Map<Integer, CreditTransaction> transactions = new ConcurrentHashMap<>();
    public final Map<Integer, Notification> notifications = new ConcurrentHashMap<>();

    public final AtomicInteger historyIdGen = new AtomicInteger(100);
    public final AtomicInteger feedbackIdGen = new AtomicInteger(100);
    public final AtomicInteger transactionIdGen = new AtomicInteger(100);
    public final AtomicInteger notificationIdGen = new AtomicInteger(100);

    private InMemoryStorage() {
        seedInitialData();
    }

    public static InMemoryStorage getInstance() {
        return INSTANCE;
    }

    /**
     * Seeds initial data matching sql/seed_data.sql.
     */
    private void seedInitialData() {
        // 1. Departments
        Department d1 = new Department(1, "Hostel & Residential Life", "HOSTEL", "hostel.admin@campus.edu", "Manages student accommodation, room maintenance, and hostel facilities.");
        Department d2 = new Department(2, "Electrical & Power Systems", "ELEC", "electrical.maint@campus.edu", "Handles campus power supply, lighting, wiring, and electrical infrastructure.");
        Department d3 = new Department(3, "IT Infrastructure & Wi-Fi", "IT_INFRA", "ithelpdesk@campus.edu", "Responsible for campus network, Wi-Fi access points, and computer labs.");
        Department d4 = new Department(4, "Estate & Water Works", "ESTATE", "estate.water@campus.edu", "Oversees plumbing, water purifiers, sanitation, and building repairs.");
        Department d5 = new Department(5, "Food & Canteen Committee", "CANTEEN", "canteen.hygiene@campus.edu", "Monitors cafeteria hygiene, food quality, pricing, and cleanliness.");
        Department d6 = new Department(6, "Campus Safety & Anti-Ragging Cell", "SAFETY", "antiragging.cell@campus.edu", "Confidential unit handling campus security, harassment, and ragging complaints.");

        departments.put(1, d1);
        departments.put(2, d2);
        departments.put(3, d3);
        departments.put(4, d4);
        departments.put(5, d5);
        departments.put(6, d6);

        // 2. Admin (password: admin123)
        Admin admin = new Admin("ADM-001", "admin", PasswordSecurityUtil.hashPassword("admin123"), "Prof. Arvind Sharma", "arvind.sharma@campus.edu", "Chief Grievance Officer");
        admins.put(admin.getId(), admin);

        // 3. Staff (password: staff123)
        DepartmentStaff stf1 = new DepartmentStaff("STF-001", 1, "EMP-HOSTEL-01", "Ramesh Kumar", "ramesh.hostel@campus.edu", PasswordSecurityUtil.hashPassword("staff123"), "+91 9876543210");
        stf1.setDepartmentName(d1.getName());
        DepartmentStaff stf2 = new DepartmentStaff("STF-002", 2, "EMP-ELEC-01", "Suresh Patel", "suresh.elec@campus.edu", PasswordSecurityUtil.hashPassword("staff123"), "+91 9876543211");
        stf2.setDepartmentName(d2.getName());
        DepartmentStaff stf3 = new DepartmentStaff("STF-003", 3, "EMP-IT-01", "Pooja Verma", "pooja.it@campus.edu", PasswordSecurityUtil.hashPassword("staff123"), "+91 9876543212");
        stf3.setDepartmentName(d3.getName());
        DepartmentStaff stf4 = new DepartmentStaff("STF-004", 4, "EMP-ESTATE-01", "Mahesh Yadav", "mahesh.estate@campus.edu", PasswordSecurityUtil.hashPassword("staff123"), "+91 9876543213");
        stf4.setDepartmentName(d4.getName());
        DepartmentStaff stf5 = new DepartmentStaff("STF-005", 5, "EMP-CANTEEN-01", "Anita Desai", "anita.canteen@campus.edu", PasswordSecurityUtil.hashPassword("staff123"), "+91 9876543214");
        stf5.setDepartmentName(d5.getName());
        DepartmentStaff stf6 = new DepartmentStaff("STF-006", 6, "EMP-SAFETY-01", "Dr. Sunita Rao", "sunita.safety@campus.edu", PasswordSecurityUtil.hashPassword("staff123"), "+91 9876543215");
        stf6.setDepartmentName(d6.getName());

        staff.put(stf1.getId(), stf1);
        staff.put(stf2.getId(), stf2);
        staff.put(stf3.getId(), stf3);
        staff.put(stf4.getId(), stf4);
        staff.put(stf5.getId(), stf5);
        staff.put(stf6.getId(), stf6);

        // 4. Students (password: password123)
        Student s1 = new Student("STU-001", "22CS0101", "Aarav Mehta", "aarav.mehta@student.campus.edu", PasswordSecurityUtil.hashPassword("password123"), "+91 9123456780", "Computer Science & Engineering", 5);
        Student s2 = new Student("STU-002", "22EC0204", "Diya Nair", "diya.nair@student.campus.edu", PasswordSecurityUtil.hashPassword("password123"), "+91 9123456781", "Electronics & Communication", 5);
        Student s3 = new Student("STU-003", "23ME0305", "Rohan Gupta", "rohan.gupta@student.campus.edu", PasswordSecurityUtil.hashPassword("password123"), "+91 9123456782", "Mechanical Engineering", 3);

        students.put(s1.getId(), s1);
        students.put(s2.getId(), s2);
        students.put(s3.getId(), s3);

        // 5. Wallets & Transactions
        CreditWallet w1 = new CreditWallet("WLT-001", s1.getId(), 15, 15, LocalDateTime.now());
        CreditWallet w2 = new CreditWallet("WLT-002", s2.getId(), 10, 10, LocalDateTime.now());
        CreditWallet w3 = new CreditWallet("WLT-003", s3.getId(), 5, 5, LocalDateTime.now());

        wallets.put(w1.getWalletId(), w1);
        wallets.put(w2.getWalletId(), w2);
        wallets.put(w3.getWalletId(), w3);

        s1.setWallet(w1);
        s2.setWallet(w2);
        s3.setWallet(w3);

        recordTx(w1, "CMP-001", TransactionType.COMPLAINT_SUBMISSION, 5, "Initial submission reward for CMP-2026-001");
        recordTx(w1, "CMP-001", TransactionType.VERIFIED_SAFETY_BONUS, 5, "Verified electrical safety hazard bonus CMP-2026-001");
        recordTx(w1, "CMP-001", TransactionType.RESOLUTION_FEEDBACK, 2, "Feedback provided for resolved ticket CMP-2026-001");
        recordTx(w1, "CMP-001", TransactionType.VERIFICATION_HELP, 3, "Assisted department in verifying electrical repair");

        recordTx(w2, "CMP-002", TransactionType.COMPLAINT_SUBMISSION, 5, "Valid complaint submission for CMP-2026-002");
        recordTx(w2, "CMP-002", TransactionType.SUPPLEMENTARY_INFO, 3, "Uploaded detailed photos and room location");
        recordTx(w2, "CMP-002", TransactionType.RESOLUTION_FEEDBACK, 2, "Feedback provided for CMP-2026-002");

        recordTx(w3, "CMP-003", TransactionType.COMPLAINT_SUBMISSION, 5, "Valid complaint submission for CMP-2026-003");

        // 6. Complaints
        Complaint c1 = new Complaint("CMP-001", "CMP-2026-001", s1.getId(), s1.getName(), s1.getRollNumber(),
                ComplaintCategory.WATER_ELECTRICITY, "Exposed High Voltage Wire near Lab 304",
                "Exposed electrical wires hanging near the entrance of Computer Lab 304, sparking occasionally.",
                "Academic Block B (ECE & EEE)", PriorityLevel.URGENT, ComplaintStatus.RESOLVED, false,
                2, d2.getName(), stf2.getId(), stf2.getName(),
                LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(3), null);

        Complaint c2 = new Complaint("CMP-002", "CMP-2026-002", s2.getId(), s2.getName(), s2.getRollNumber(),
                ComplaintCategory.HOSTEL, "Water Leakage in Hostel Block C Washroom",
                "Continuous pipe leakage in 2nd floor bathroom causing water accumulation and slipping hazard.",
                "Hostel Block C (Girls)", PriorityLevel.HIGH, ComplaintStatus.IN_PROGRESS, false,
                1, d1.getName(), stf1.getId(), stf1.getName(),
                LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(1), null, null);

        Complaint c3 = new Complaint("CMP-003", "CMP-2026-003", s3.getId(), s3.getName(), s3.getRollNumber(),
                ComplaintCategory.WIFI_TECHNICAL, "Wi-Fi Signal Dropping in Central Library",
                "Signal keeps disconnecting every 5 minutes in the digital reading section.",
                "Central Library & Digital Resource Center", PriorityLevel.NORMAL, ComplaintStatus.SUBMITTED, false,
                null, null, null, null,
                LocalDateTime.now().minusHours(5), LocalDateTime.now().plusDays(6), null, null);

        Complaint c4 = new Complaint("CMP-004", "CMP-2026-004", s1.getId(), s1.getName(), s1.getRollNumber(),
                ComplaintCategory.RAGGING_HARASSMENT, "Intimidation near North Gate parking after 8 PM",
                "Senior students gathering near dark section of North Gate demanding personal belongings.",
                "North Gate Parking & Bus Bay", PriorityLevel.URGENT, ComplaintStatus.UNDER_REVIEW, true,
                6, d6.getName(), stf6.getId(), stf6.getName(),
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusHours(12), null, null);

        complaints.put(c1.getComplaintId(), c1);
        complaints.put(c2.getComplaintId(), c2);
        complaints.put(c3.getComplaintId(), c3);
        complaints.put(c4.getComplaintId(), c4);

        // 7. Complaint History
        recordHistory(c1.getComplaintId(), "SUBMITTED", "ASSIGNED", "Assigned with high urgency to Electrical Maintenance department.", admin.getName(), "ADMIN");
        recordHistory(c1.getComplaintId(), "ASSIGNED", "IN_PROGRESS", "Inspection started; main switch breaker isolated.", stf2.getName(), "STAFF");
        recordHistory(c1.getComplaintId(), "IN_PROGRESS", "RESOLVED", "Insulated conduit installed, replaced burnt socket and retested.", stf2.getName(), "STAFF");

        recordHistory(c2.getComplaintId(), "SUBMITTED", "ASSIGNED", "Forwarded to Hostel Estate team.", admin.getName(), "ADMIN");
        recordHistory(c2.getComplaintId(), "ASSIGNED", "IN_PROGRESS", "Plumber dispatched with replacement PVC joints.", stf1.getName(), "STAFF");

        // 8. Feedback
        Feedback fb1 = new Feedback(feedbackIdGen.incrementAndGet(), c1.getComplaintId(), s1.getId(), 5, "Prompt resolution within 24 hours. Very grateful for rapid electrical hazard fix!", LocalDateTime.now().minusDays(2));
        feedbacks.put(fb1.getFeedbackId(), fb1);

        // 9. Notifications
        Notification n1 = new Notification(notificationIdGen.incrementAndGet(), s1.getId(), UserRole.STUDENT, "Complaint Resolved (CMP-2026-001)", "Your complaint regarding exposed wire near Lab 304 has been resolved. You earned +2 feedback credits!", true, LocalDateTime.now().minusDays(2));
        Notification n2 = new Notification(notificationIdGen.incrementAndGet(), s2.getId(), UserRole.STUDENT, "Work in Progress (CMP-2026-002)", "Hostel staff Ramesh Kumar has commenced work on your washroom leakage complaint.", false, LocalDateTime.now().minusDays(1));
        Notification n3 = new Notification(notificationIdGen.incrementAndGet(), stf2.getId(), UserRole.STAFF, "Urgent Ticket Assigned", "Urgent complaint CMP-2026-001 assigned to you.", true, LocalDateTime.now().minusDays(3));
        Notification n4 = new Notification(notificationIdGen.incrementAndGet(), admin.getId(), UserRole.ADMIN, "New Sensitive Complaint Filed", "Confidential complaint CMP-2026-004 filed under Ragging & Harassment category.", false, LocalDateTime.now().minusDays(1));

        notifications.put(n1.getNotificationId(), n1);
        notifications.put(n2.getNotificationId(), n2);
        notifications.put(n3.getNotificationId(), n3);
        notifications.put(n4.getNotificationId(), n4);
    }

    private void recordTx(CreditWallet wallet, String complaintId, TransactionType type, int credits, String desc) {
        int id = transactionIdGen.incrementAndGet();
        CreditTransaction tx = new CreditTransaction(id, wallet.getWalletId(), complaintId, type, credits, desc, LocalDateTime.now());
        transactions.put(id, tx);
        wallet.getTransactions().add(tx);
    }

    private void recordHistory(String complaintId, String from, String to, String remarks, String name, String role) {
        int id = historyIdGen.incrementAndGet();
        ComplaintHistory ch = new ComplaintHistory(id, complaintId, from, to, remarks, name, role, LocalDateTime.now());
        complaintHistories.put(id, ch);
    }
}
