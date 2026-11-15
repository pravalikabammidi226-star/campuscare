package com.campuscare.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing the student's CampusCare Credit Wallet.
 * Tracks non-monetary gamified participation points and reward status.
 */
public class CreditWallet implements Serializable {
    private static final long serialVersionUID = 1L;

    private String walletId;
    private String studentId;
    private int totalBalance;
    private int lifetimeEarned;
    private LocalDateTime updatedAt;
    private List<CreditTransaction> transactions;

    public CreditWallet() {
        this.totalBalance = 0;
        this.lifetimeEarned = 0;
        this.updatedAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();
    }

    public CreditWallet(String walletId, String studentId) {
        this.walletId = walletId;
        this.studentId = studentId;
        this.totalBalance = 0;
        this.lifetimeEarned = 0;
        this.updatedAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();
    }

    public CreditWallet(String walletId, String studentId, int totalBalance, int lifetimeEarned, LocalDateTime updatedAt) {
        this.walletId = walletId;
        this.studentId = studentId;
        this.totalBalance = totalBalance;
        this.lifetimeEarned = lifetimeEarned;
        this.updatedAt = updatedAt;
        this.transactions = new ArrayList<>();
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(int totalBalance) {
        this.totalBalance = totalBalance;
    }

    public int getLifetimeEarned() {
        return lifetimeEarned;
    }

    public void setLifetimeEarned(int lifetimeEarned) {
        this.lifetimeEarned = lifetimeEarned;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CreditTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<CreditTransaction> transactions) {
        this.transactions = transactions;
    }

    public void addCredits(int points) {
        if (points > 0) {
            this.totalBalance += points;
            this.lifetimeEarned += points;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public String getTierBadge() {
        if (lifetimeEarned >= 100) return "Campus Champion (Gold)";
        if (lifetimeEarned >= 50) return "Active Contributor (Silver)";
        if (lifetimeEarned >= 20) return "Responsible Peer (Bronze)";
        return "Beginner Citizen";
    }

    @Override
    public String toString() {
        return String.format("Wallet[ID: %s, Balance: %d, Lifetime: %d, Tier: %s]",
                walletId, totalBalance, lifetimeEarned, getTierBadge());
    }
}
