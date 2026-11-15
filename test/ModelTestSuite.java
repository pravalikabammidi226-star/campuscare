package test;

import com.campuscare.model.*;
import com.campuscare.model.enums.*;
import java.time.LocalDateTime;

/**
 * Unit tests for domain models, inheritance, polymorphism, and encapsulation.
 */
public class ModelTestSuite {

    public static boolean runTests() {
        System.out.println("  [Test Group 1] Domain Models & OOP Principles...");
        boolean allPassed = true;

        // 1. Inheritance & Polymorphism
        User student = new Student("STU-999", "22CS9999", "Test Student", "test@campus.edu", "hash", "+91 9999999999", "CSE", 5);
        User admin = new Admin("ADM-999", "superadmin", "hash", "Dr. Admin", "admin@campus.edu", "Director");
        User staff = new DepartmentStaff("STF-999", 2, "EMP-999", "Tech Staff", "tech@campus.edu", "hash", "+91 8888888888");

        assertCondition(student.getRole() == UserRole.STUDENT, "Student role check");
        assertCondition(student.getRoleBadge().contains("22CS9999"), "Student polymorphic badge");
        assertCondition(admin.getRole() == UserRole.ADMIN, "Admin role check");
        assertCondition(admin.getRoleBadge().contains("Director"), "Admin polymorphic badge");
        assertCondition(staff.getRole() == UserRole.STAFF, "Staff role check");

        // 2. Encapsulation & Wallet logic
        CreditWallet wallet = new CreditWallet("WLT-999", "STU-999");
        assertCondition(wallet.getTotalBalance() == 0, "Initial wallet balance zero");
        wallet.addCredits(15);
        assertCondition(wallet.getTotalBalance() == 15, "Wallet balance addition");
        assertCondition(wallet.getLifetimeEarned() == 15, "Wallet lifetime points");
        assertCondition(wallet.getTierBadge().contains("Bronze") || wallet.getTierBadge().contains("Beginner"), "Tier badge computation");

        // 3. Complaint SLA & Deadline
        Complaint c = new Complaint("CMP-TEST", "CMP-2026-999", "STU-999", ComplaintCategory.WATER_ELECTRICITY,
                "Broken pipe", "Continuous flooding", "Hostel Block A", PriorityLevel.HIGH, false,
                LocalDateTime.now().minusHours(2)); // already expired

        assertCondition(c.isOverdue(), "Overdue complaint detection");

        // 4. Anonymous masking
        Complaint cAnon = new Complaint("CMP-ANON", "CMP-2026-998", "STU-999", ComplaintCategory.RAGGING_HARASSMENT,
                "Harassment report", "Sensitive incident", "North Gate", PriorityLevel.URGENT, true,
                LocalDateTime.now().plusHours(12));
        cAnon.setStudentName("Test Student");
        cAnon.setStudentRoll("22CS9999");

        assertCondition(cAnon.getDisplayAuthor(false).contains("Anonymous"), "Confidential author masking for public/staff");
        assertCondition(cAnon.getDisplayAuthor(true).contains("Test Student"), "Admin unmasked author inspection");

        System.out.println("    ✔ ModelTestSuite passed all assertions.");
        return allPassed;
    }

    private static void assertCondition(boolean condition, String testName) {
        if (!condition) {
            throw new AssertionError("FAILED assertion: " + testName);
        }
    }
}
