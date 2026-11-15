package test;

import com.campuscare.dao.DaoFactory;
import com.campuscare.model.CreditTransaction;
import com.campuscare.model.CreditWallet;
import com.campuscare.model.enums.TransactionType;
import com.campuscare.service.CreditWalletService;
import java.util.List;

/**
 * Unit tests for student credit rewards, duplicate claim prevention, and leaderboard ranking.
 */
public class CreditWalletTest {

    public static boolean runTests() {
        System.out.println("  [Test Group 4] CampusCare Credit Wallet & Anti-Abuse Rules...");
        // Ensure in-memory mode for standalone testing
        DaoFactory.getInstance().forceMemoryMode();
        CreditWalletService walletService = new CreditWalletService();

        String testStudentId = "STU-001";
        CreditWallet initialWallet = walletService.getWalletByStudentId(testStudentId);
        int initialBalance = initialWallet.getTotalBalance();

        try {
            // 1. Award complaint credits (+5)
            String dummyTicket = "TEST-TICKET-01";
            boolean awarded1 = walletService.awardCredits(testStudentId, dummyTicket, TransactionType.COMPLAINT_SUBMISSION, "Valid submission");
            assertCondition(awarded1, "First submission credit award succeeds");

            CreditWallet updatedWallet = walletService.getWalletByStudentId(testStudentId);
            assertCondition(updatedWallet.getTotalBalance() == initialBalance + 5, "Balance increased by +5");

            // 2. Anti-abuse duplicate prevention: repeated claim for same ticket & type must be blocked!
            boolean duplicateClaim = walletService.awardCredits(testStudentId, dummyTicket, TransactionType.COMPLAINT_SUBMISSION, "Duplicate submission attempt");
            assertCondition(!duplicateClaim, "Repeated claim for same ticket and type must be BLOCKED");

            // 3. Award feedback credits (+2) for same ticket is allowed (different transaction type)
            boolean feedbackAward = walletService.awardCredits(testStudentId, dummyTicket, TransactionType.RESOLUTION_FEEDBACK, "Feedback given");
            assertCondition(feedbackAward, "Feedback credit award allowed on same ticket");

            // 4. Verify transaction ledger
            List<CreditTransaction> history = walletService.getTransactionHistory(testStudentId);
            assertCondition(!history.isEmpty(), "Transaction history is populated");
            assertCondition(history.get(0).getCredits() > 0, "Transaction credits are positive");

            // 5. Leaderboard sorting
            List<CreditWalletService.LeaderboardEntry> leaderboard = walletService.getLeaderboard(10);
            assertCondition(!leaderboard.isEmpty(), "Leaderboard has entries");
            assertCondition(leaderboard.get(0).getLifetimeCredits() >= leaderboard.get(leaderboard.size() - 1).getLifetimeCredits(),
                    "Leaderboard properly sorted descending by lifetime points");

            System.out.println("    ✔ CreditWalletTest passed all assertions.");
            return true;

        } catch (Exception ex) {
            throw new RuntimeException("CreditWalletTest failed", ex);
        }
    }

    private static void assertCondition(boolean condition, String testName) {
        if (!condition) {
            throw new AssertionError("FAILED assertion: " + testName);
        }
    }
}
