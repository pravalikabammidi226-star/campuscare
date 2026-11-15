package com.campuscare.service;

import com.campuscare.dao.CreditWalletDao;
import com.campuscare.dao.DaoFactory;
import com.campuscare.dao.NotificationDao;
import com.campuscare.dao.StudentDao;
import com.campuscare.exception.ValidationException;
import com.campuscare.model.CreditTransaction;
import com.campuscare.model.CreditWallet;
import com.campuscare.model.Notification;
import com.campuscare.model.Student;
import com.campuscare.model.enums.TransactionType;
import com.campuscare.model.enums.UserRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service managing student credit rewards, wallet balances, anti-spam validation, and leaderboards.
 */
public class CreditWalletService {

    private final CreditWalletDao walletDao;
    private final StudentDao studentDao;
    private final NotificationDao notificationDao;

    public CreditWalletService() {
        DaoFactory factory = DaoFactory.getInstance();
        this.walletDao = factory.getCreditWalletDao();
        this.studentDao = factory.getStudentDao();
        this.notificationDao = factory.getNotificationDao();
    }

    public CreditWalletService(CreditWalletDao walletDao, StudentDao studentDao, NotificationDao notificationDao) {
        this.walletDao = walletDao;
        this.studentDao = studentDao;
        this.notificationDao = notificationDao;
    }

    /**
     * Gets or lazily creates a credit wallet for a student.
     */
    public CreditWallet getWalletByStudentId(String studentId) {
        return walletDao.findByStudentId(studentId).orElseGet(() -> {
            CreditWallet newWallet = new CreditWallet("WLT-" + studentId, studentId);
            return walletDao.save(newWallet);
        });
    }

    /**
     * Awards participation credits with duplicate prevention.
     * @return boolean indicating whether credits were awarded (false if already collected)
     */
    public synchronized boolean awardCredits(String studentId, String complaintId, TransactionType type, String customDescription) throws ValidationException {
        if (studentId == null || type == null) {
            throw new ValidationException("CreditReward", "Student ID and Transaction Type are required.");
        }

        CreditWallet wallet = getWalletByStudentId(studentId);

        // Anti-abuse check: prevent repeated credit claims for the same ticket and type
        if (complaintId != null && walletDao.hasTransactionForComplaint(wallet.getWalletId(), complaintId, type)) {
            return false; // Already awarded for this ticket
        }

        int points = type.getDefaultPoints();
        String description = customDescription != null && !customDescription.trim().isEmpty()
                ? customDescription
                : type.getDescription();

        CreditTransaction transaction = new CreditTransaction(wallet.getWalletId(), complaintId, type, points, description);
        walletDao.recordTransaction(transaction);

        // Notify student of credit award
        Notification notif = new Notification(
                studentId,
                UserRole.STUDENT,
                "CampusCare Credits Earned (+" + points + " pts)",
                "You earned +" + points + " credits for: " + description + ". Current balance: " + (wallet.getTotalBalance() + points)
        );
        notificationDao.save(notif);

        return true;
    }

    /**
     * Retrieves full transaction ledger history for a student.
     */
    public List<CreditTransaction> getTransactionHistory(String studentId) {
        CreditWallet wallet = getWalletByStudentId(studentId);
        return walletDao.findTransactionsByWalletId(wallet.getWalletId());
    }

    /**
     * Generates the live campus leaderboard of top student contributors.
     */
    public List<LeaderboardEntry> getLeaderboard(int limit) {
        List<CreditWallet> topWallets = walletDao.getLeaderboard(limit);
        List<LeaderboardEntry> entries = new ArrayList<>();
        int rank = 1;

        for (CreditWallet w : topWallets) {
            Optional<Student> studentOpt = studentDao.findById(w.getStudentId());
            String studentName = studentOpt.map(Student::getName).orElse("Student (" + w.getStudentId() + ")");
            String rollNumber = studentOpt.map(Student::getRollNumber).orElse("N/A");
            String department = studentOpt.map(Student::getDepartment).orElse("General");

            entries.add(new LeaderboardEntry(
                    rank++,
                    studentName,
                    rollNumber,
                    department,
                    w.getTotalBalance(),
                    w.getLifetimeEarned(),
                    w.getTierBadge()
            ));
        }
        return entries;
    }

    public static class LeaderboardEntry {
        private final int rank;
        private final String studentName;
        private final String rollNumber;
        private final String department;
        private final int balance;
        private final int lifetimeCredits;
        private final String tierBadge;

        public LeaderboardEntry(int rank, String studentName, String rollNumber, String department, int balance, int lifetimeCredits, String tierBadge) {
            this.rank = rank;
            this.studentName = studentName;
            this.rollNumber = rollNumber;
            this.department = department;
            this.balance = balance;
            this.lifetimeCredits = lifetimeCredits;
            this.tierBadge = tierBadge;
        }

        public int getRank() { return rank; }
        public String getStudentName() { return studentName; }
        public String getRollNumber() { return rollNumber; }
        public String getDepartment() { return department; }
        public int getBalance() { return balance; }
        public int getLifetimeCredits() { return lifetimeCredits; }
        public String getTierBadge() { return tierBadge; }
    }
}
