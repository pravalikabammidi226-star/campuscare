package test;

import com.campuscare.model.enums.ComplaintCategory;
import com.campuscare.model.enums.PriorityLevel;
import com.campuscare.service.SmartPriorityService;

/**
 * Unit tests for intelligent keyword scoring, safety risk evaluation, and SLA calculations.
 */
public class SmartPriorityTest {

    public static boolean runTests() {
        System.out.println("  [Test Group 3] Smart Priority Classification & SLA Deadlines...");
        SmartPriorityService service = new SmartPriorityService();

        // 1. Mandatory Ragging / Harassment Urgency Rule
        SmartPriorityService.PriorityEvaluation eval1 = service.evaluatePriority(
                ComplaintCategory.RAGGING_HARASSMENT,
                "Ragging complaint near hostel",
                "Senior students asking for money",
                PriorityLevel.NORMAL
        );
        assertCondition(eval1.getComputedPriority() == PriorityLevel.URGENT, "Ragging category must always be URGENT");

        // 2. Critical Safety Hazard Keywords (Fire, Spark, Electric shock)
        SmartPriorityService.PriorityEvaluation eval2 = service.evaluatePriority(
                ComplaintCategory.CLASSROOM_LAB,
                "Electrical switch sparking near door",
                "Spark and smoke coming from circuit board in Lab 102",
                PriorityLevel.NORMAL
        );
        assertCondition(eval2.getComputedPriority() == PriorityLevel.URGENT, "Spark/smoke keyword triggers URGENT");

        // 3. High Urgency Keywords (Flooding, Leakage)
        SmartPriorityService.PriorityEvaluation eval3 = service.evaluatePriority(
                ComplaintCategory.HOSTEL,
                "Washroom water leakage",
                "Flooding in the corridor from broken pipe",
                PriorityLevel.NORMAL
        );
        assertCondition(eval3.getComputedPriority() == PriorityLevel.HIGH, "Flooding/leakage triggers HIGH");

        // 4. Standard Category SLA
        SmartPriorityService.PriorityEvaluation eval4 = service.evaluatePriority(
                ComplaintCategory.LIBRARY,
                "Bookshelf label missing",
                "History section label fallen off shelf",
                PriorityLevel.NORMAL
        );
        assertCondition(eval4.getComputedPriority() == PriorityLevel.NORMAL, "Standard non-hazardous issue is NORMAL");

        // 5. SLA Deadline calculation check
        assertCondition(PriorityLevel.URGENT.getSlaHours() == 24, "Urgent SLA is 24 hours");
        assertCondition(PriorityLevel.HIGH.getSlaHours() == 72, "High SLA is 72 hours");
        assertCondition(PriorityLevel.NORMAL.getSlaHours() == 168, "Normal SLA is 168 hours (7 days)");

        System.out.println("    ✔ SmartPriorityTest passed all assertions.");
        return true;
    }

    private static void assertCondition(boolean condition, String testName) {
        if (!condition) {
            throw new AssertionError("FAILED assertion: " + testName);
        }
    }
}
