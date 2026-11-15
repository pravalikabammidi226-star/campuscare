package com.campuscare.dao.memory;

import com.campuscare.dao.CreditWalletDao;
import com.campuscare.model.CreditTransaction;
import com.campuscare.model.CreditWallet;
import com.campuscare.model.enums.TransactionType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CreditWalletMemoryDao implements CreditWalletDao {
    private final InMemoryStorage storage = InMemoryStorage.getInstance();

    @Override
    public Optional<CreditWallet> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.wallets.get(id));
    }

    @Override
    public List<CreditWallet> findAll() {
        return new ArrayList<>(storage.wallets.values());
    }

    @Override
    public CreditWallet save(CreditWallet entity) {
        if (entity != null && entity.getWalletId() != null) {
            storage.wallets.put(entity.getWalletId(), entity);
        }
        return entity;
    }

    @Override
    public boolean update(CreditWallet entity) {
        if (entity != null && entity.getWalletId() != null && storage.wallets.containsKey(entity.getWalletId())) {
            storage.wallets.put(entity.getWalletId(), entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(String id) {
        return storage.wallets.remove(id) != null;
    }

    @Override
    public Optional<CreditWallet> findByStudentId(String studentId) {
        if (studentId == null) return Optional.empty();
        return storage.wallets.values().stream()
                .filter(w -> studentId.equals(w.getStudentId()))
                .findFirst();
    }

    @Override
    public CreditTransaction recordTransaction(CreditTransaction transaction) {
        if (transaction != null) {
            if (transaction.getTransactionId() <= 0) {
                transaction.setTransactionId(storage.transactionIdGen.incrementAndGet());
            }
            storage.transactions.put(transaction.getTransactionId(), transaction);

            // update wallet balance
            CreditWallet wallet = storage.wallets.get(transaction.getWalletId());
            if (wallet != null) {
                wallet.addCredits(transaction.getCredits());
                wallet.getTransactions().add(transaction);
            }
        }
        return transaction;
    }

    @Override
    public List<CreditTransaction> findTransactionsByWalletId(String walletId) {
        return storage.transactions.values().stream()
                .filter(t -> walletId.equals(t.getWalletId()))
                .sorted(Comparator.comparing(CreditTransaction::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasTransactionForComplaint(String walletId, String complaintId, TransactionType type) {
        if (walletId == null || complaintId == null || type == null) return false;
        return storage.transactions.values().stream()
                .anyMatch(t -> walletId.equals(t.getWalletId()) &&
                               complaintId.equals(t.getComplaintId()) &&
                               t.getTransactionType() == type);
    }

    @Override
    public List<CreditWallet> getLeaderboard(int limit) {
        return storage.wallets.values().stream()
                .sorted(Comparator.comparing(CreditWallet::getLifetimeEarned).reversed())
                .limit(limit > 0 ? limit : 10)
                .collect(Collectors.toList());
    }
}
