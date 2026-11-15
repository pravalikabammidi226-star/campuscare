package com.campuscare.model;

import com.campuscare.model.enums.TransactionType;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Immutable ledger entry recording credit accrual for students.
 */
public class CreditTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private int transactionId;
    private String walletId;
    private String complaintId;
    private TransactionType transactionType;
    private int credits;
    private String description;
    private LocalDateTime timestamp;

    public CreditTransaction() {
        this.timestamp = LocalDateTime.now();
    }

    public CreditTransaction(String walletId, String complaintId, TransactionType transactionType,
                             int credits, String description) {
        this.walletId = walletId;
        this.complaintId = complaintId;
        this.transactionType = transactionType;
        this.credits = credits;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public CreditTransaction(int transactionId, String walletId, String complaintId,
                             TransactionType transactionType, int credits, String description,
                             LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.walletId = walletId;
        this.complaintId = complaintId;
        this.transactionType = transactionType;
        this.credits = credits;
        this.description = description;
        this.timestamp = timestamp;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("+%d Credits: %s [%s]", credits, description, transactionType);
    }
}
