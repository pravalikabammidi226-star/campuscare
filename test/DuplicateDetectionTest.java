package test;

import com.campuscare.model.Complaint;
import com.campuscare.model.enums.ComplaintCategory;
import com.campuscare.model.enums.PriorityLevel;
import com.campuscare.util.TextSimilarityUtil;
import java.time.LocalDateTime;

/**
 * Unit tests for natural language tokenization, Jaccard similarity, and duplicate detection scoring.
 */
public class DuplicateDetectionTest {

    public static boolean runTests() {
        System.out.println("  [Test Group 2] Duplicate Complaint Detection & Similarity Algorithms...");

        // 1. Keyword extraction & Stopword stripping
        var tokens = TextSimilarityUtil.extractKeywords("The water leakage in the washroom is very heavy and flooding the floor!");
        assertCondition(tokens.contains("water"), "Token contains water");
        assertCondition(tokens.contains("leakage"), "Token contains leakage");
        assertCondition(tokens.contains("washroom"), "Token contains washroom");
        assertCondition(!tokens.contains("the"), "Stopword 'the' stripped");
        assertCondition(!tokens.contains("and"), "Stopword 'and' stripped");

        // 2. Jaccard similarity
        double simHigh = TextSimilarityUtil.computeJaccardSimilarity(
                "Water pipe leakage in hostel bathroom",
                "Continuous pipe leakage and water in hostel bathroom"
        );
        assertCondition(simHigh > 0.40, "High semantic token overlap (" + simHigh + ")");

        double simLow = TextSimilarityUtil.computeJaccardSimilarity(
                "Water leaking from bathroom pipe",
                "Wi-Fi router disconnected in Central Library"
        );
        assertCondition(simLow < 0.10, "Disjoint semantic topics have low similarity (" + simLow + ")");

        // 3. Composite Duplicate Scoring
        Complaint c1 = new Complaint("C1", "CMP-01", "S1", ComplaintCategory.HOSTEL,
                "Water Leakage in Hostel Block C Washroom",
                "Continuous pipe leakage in 2nd floor bathroom causing water accumulation",
                "Hostel Block C", PriorityLevel.HIGH, false, LocalDateTime.now().plusDays(1));

        Complaint c2Similar = new Complaint("C2", "CMP-02", "S2", ComplaintCategory.HOSTEL,
                "Pipe leaking water in Hostel C bathroom",
                "Continuous pipe leakage and water accumulation on 2nd floor washroom",
                "Hostel Block C", PriorityLevel.HIGH, false, LocalDateTime.now().plusDays(1));

        Complaint c3Distinct = new Complaint("C3", "CMP-03", "S3", ComplaintCategory.WIFI_TECHNICAL,
                "Wi-Fi connection drop in Library",
                "Network down in reading room",
                "Central Library", PriorityLevel.NORMAL, false, LocalDateTime.now().plusDays(3));

        double duplicateScore = TextSimilarityUtil.calculateDuplicateScore(c2Similar, c1);
        assertCondition(duplicateScore >= 0.70, "Duplicate complaint detected (score: " + duplicateScore + " >= 0.70)");

        double distinctScore = TextSimilarityUtil.calculateDuplicateScore(c3Distinct, c1);
        assertCondition(distinctScore < 0.30, "Distinct complaint not flagged (score: " + distinctScore + " < 0.30)");

        System.out.println("    ✔ DuplicateDetectionTest passed all assertions.");
        return true;
    }

    private static void assertCondition(boolean condition, String testName) {
        if (!condition) {
            throw new AssertionError("FAILED assertion: " + testName);
        }
    }
}
