package test;

/**
 * Automated test suite runner for CampusCare+.
 * Executes all regression and domain unit tests and prints a consolidated report.
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   CAMPUSCARE+ AUTOMATED REGRESSION & INTEGRATION TEST SUITE                    ");
        System.out.println("   Java 21 LTS / Pure Core Java Architecture                                   ");
        System.out.println("================================================================================");

        long start = System.currentTimeMillis();
        int passed = 0;
        int failed = 0;

        try {
            if (ModelTestSuite.runTests()) passed++; else failed++;
        } catch (Throwable t) {
            System.err.println("❌ ModelTestSuite FAILED: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            if (DuplicateDetectionTest.runTests()) passed++; else failed++;
        } catch (Throwable t) {
            System.err.println("❌ DuplicateDetectionTest FAILED: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            if (SmartPriorityTest.runTests()) passed++; else failed++;
        } catch (Throwable t) {
            System.err.println("❌ SmartPriorityTest FAILED: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            if (CreditWalletTest.runTests()) passed++; else failed++;
        } catch (Throwable t) {
            System.err.println("❌ CreditWalletTest FAILED: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            if (ServiceTestSuite.runTests()) passed++; else failed++;
        } catch (Throwable t) {
            System.err.println("❌ ServiceTestSuite FAILED: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("================================================================================");
        System.out.println(String.format("   TEST RUN COMPLETE in %d ms", elapsed));
        System.out.println(String.format("   Results: %d Passed, %d Failed (Total %d test suites)", passed, failed, (passed + failed)));
        System.out.println("================================================================================");

        if (failed > 0) {
            System.exit(1);
        } else {
            System.out.println("🎉 ALL AUTOMATED VERIFICATION CHECKS PASSED PERFECTLY!");
        }
    }
}
