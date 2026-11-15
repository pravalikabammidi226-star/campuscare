package com.campuscare.dao;

import com.campuscare.model.CreditTransaction;
import com.campuscare.model.CreditWallet;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Credit Wallets and Transactions.
 */
public interface CreditWalletDao extends GenericDao<CreditWallet, String> {
    Optional<CreditWallet> findByStudentId(String studentId);
    CreditTransaction recordTransaction(CreditTransaction transaction);
    List<CreditTransaction> findTransactionsByWalletId(String walletId);
    boolean hasTransactionForComplaint(String walletId, String complaintId, com.campuscare.model.enums.TransactionType type);
    List<CreditWallet> getLeaderboard(int limit);
}
