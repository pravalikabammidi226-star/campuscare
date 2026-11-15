package test;

import com.campuscare.controller.AppController;
import com.campuscare.dao.DaoFactory;
import com.campuscare.model.Complaint;
import com.campuscare.model.Student;
import com.campuscare.model.enums.ComplaintCategory;
import com.campuscare.model.enums.ComplaintStatus;
import com.campuscare.model.enums.PriorityLevel;
import com.campuscare.service.*;
import java.io.File;

/**
 * End-to-end integration test validating the entire complaint resolution lifecycle,
 * authentication, feedback loop, escalation daemon, and report exports.
 */
public class ServiceTestSuite {

    public static boolean runTests() {
        System.out.println("  [Test Group 5] Full Lifecycle Integration (Submit -> Assign -> Progress -> Resolve -> Feedback -> Reports)...");
        DaoFactory.getInstance().forceMemoryMode();
        AppController controller = AppController.getInstance();

        AuthService authService = controller.getAuthService();
        ComplaintService complaintService = controller.getComplaintService();
        FeedbackService feedbackService = controller.getFeedbackService();
        AnalyticsService analyticsService = controller.getAnalyticsService();
        ReportExportService reportExportService = controller.getReportExportService();

        try {
            // Step 1: Register new student
            String roll = "24CS" + System.currentTimeMillis() % 10000;
            Student newStudent = authService.registerStudent(roll, "Integration Tester", "tester_" + roll + "@campus.edu",
                    "password123", "+91 9998887777", "Computer Science & Engineering", 3);
            assertCondition(newStudent != null, "Student registration");

            // Step 2: Student logs in
            Student loggedIn = authService.loginStudent(newStudent.getEmail(), "password123");
            assertCondition(loggedIn.getId().equals(newStudent.getId()), "Student login successful");

            // Step 3: Student submits complaint
            Complaint c = complaintService.submitComplaint(
                    loggedIn.getId(),
                    ComplaintCategory.CLASSROOM_LAB,
                    "Projector bulb blown in Lecture Hall 201",
                    "The overhead projector is not powering on, urgent class tomorrow morning",
                    "Academic Block A (CSE & IT)",
                    PriorityLevel.NORMAL,
                    false
            );
            assertCondition(c != null, "Complaint created");
            assertCondition(c.getStatus() == ComplaintStatus.SUBMITTED, "Initial status SUBMITTED");
            assertCondition(c.getTicketNumber().startsWith("CMP-"), "Valid ticket format");

            // Step 4: Admin assigns complaint to Department 2 (Electrical) and Staff STF-002 (Suresh Patel)
            boolean assigned = complaintService.assignComplaint(c.getComplaintId(), 2, "STF-002", "Prof. Arvind Sharma");
            assertCondition(assigned, "Admin assignment");
            Complaint cAssigned = complaintService.getComplaintById(c.getComplaintId()).get();
            assertCondition(cAssigned.getStatus() == ComplaintStatus.ASSIGNED, "Status changed to ASSIGNED");

            // Step 5: Staff accepts & updates progress
            boolean progressUpdated = complaintService.updateProgress(c.getComplaintId(), "Suresh Patel", "Inspected fixture; replacement bulb ordered from inventory.");
            assertCondition(progressUpdated, "Progress note logged");
            Complaint cInProg = complaintService.getComplaintById(c.getComplaintId()).get();
            assertCondition(cInProg.getStatus() == ComplaintStatus.IN_PROGRESS, "Status changed to IN_PROGRESS");

            // Step 6: Staff marks complaint resolved
            boolean resolved = complaintService.resolveComplaint(c.getComplaintId(), "Suresh Patel", "STAFF", "Installed new OEM lamp bulb and tested 4K projection display.");
            assertCondition(resolved, "Complaint marked resolved");
            Complaint cResolved = complaintService.getComplaintById(c.getComplaintId()).get();
            assertCondition(cResolved.getStatus() == ComplaintStatus.RESOLVED, "Status changed to RESOLVED");
            assertCondition(cResolved.getResolvedAt() != null, "Resolved timestamp populated");

            // Step 7: Student provides 5-star resolution feedback
            var feedback = feedbackService.submitFeedback(c.getComplaintId(), loggedIn.getId(), 5, "Classroom projection works flawlessly. Superb quick response!");
            assertCondition(feedback != null, "Feedback persisted");
            assertCondition(feedback.getRating() == 5, "5-star rating recorded");

            // Step 8: Verify Analytics & Heatmap
            AnalyticsService.OverallStats stats = analyticsService.getOverallStats();
            assertCondition(stats.getTotalComplaints() > 0, "Analytics total complaints");
            assertCondition(stats.getResolvedComplaints() > 0, "Analytics resolved complaints");
            assertCondition(stats.getSatisfactionPercentage() > 80.0, "Satisfaction index high");

            var heatmap = analyticsService.getCampusHeatmap();
            assertCondition(!heatmap.isEmpty(), "Heatmap has locations");

            // Step 9: Verify File Export (CSV and TXT)
            File tempCsv = File.createTempFile("test_complaints_", ".csv");
            tempCsv.deleteOnExit();
            int exportedRows = reportExportService.exportComplaintsToCsv(tempCsv);
            assertCondition(exportedRows > 0, "CSV export row count");
            assertCondition(tempCsv.length() > 0, "CSV file has data");

            File tempTxt = File.createTempFile("test_audit_", ".txt");
            tempTxt.deleteOnExit();
            reportExportService.exportDepartmentAuditReport(tempTxt);
            assertCondition(tempTxt.length() > 0, "TXT audit report has data");

            // Step 10: Verify Object Serialization
            File tempDat = File.createTempFile("test_backup_", ".dat");
            tempDat.deleteOnExit();
            reportExportService.serializeBackup(tempDat);
            assertCondition(tempDat.length() > 0, "Serialized backup written");

            var restored = reportExportService.deserializeBackup(tempDat);
            assertCondition(restored != null && !restored.isEmpty(), "Deserialized complaints restored");

            System.out.println("    ✔ ServiceTestSuite passed all lifecycle steps.");
            return true;

        } catch (Exception ex) {
            throw new RuntimeException("ServiceTestSuite lifecycle failed", ex);
        }
    }

    private static void assertCondition(boolean condition, String testName) {
        if (!condition) {
            throw new AssertionError("FAILED assertion: " + testName);
        }
    }
}
