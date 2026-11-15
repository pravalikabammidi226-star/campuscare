package com.campuscare.dao.jdbc;

import com.campuscare.dao.CreditWalletDao;
import com.campuscare.model.CreditTransaction;
import com.campuscare.model.CreditWallet;
import com.campuscare.model.enums.TransactionType;
import com.campuscare.util.DatabaseConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreditWalletJdbcDao extends BaseJdbcDao implements CreditWalletDao {

    @Override
    public Optional<CreditWallet> findById(String id) {
        String sql = "SELECT wallet_id, student_id, total_balance, lifetime_earned, updated_at FROM credit_wallets WHERE wallet_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapWallet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs, ps, conn);
        }
        return Optional.empty();
    }

    @Override
    public List<CreditWallet> findAll() {
        List<CreditWallet> list = new ArrayList<>();
        String sql = "SELECT wallet_id, student_id, total_balance, lifetime_earned, updated_at FROM credit_wallets";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapWallet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs, stmt, conn);
        }
        return list;
    }

    @Override
    public CreditWallet save(CreditWallet w) {
        String sql = "INSERT INTO credit_wallets (wallet_id, student_id, total_balance, lifetime_earned, updated_at) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, w.getWalletId());
            ps.setString(2, w.getStudentId());
            ps.setInt(3, w.getTotalBalance());
            ps.setInt(4, w.getLifetimeEarned());
            ps.setTimestamp(5, toTimestamp(w.getUpdatedAt()));
            ps.executeUpdate();
            return w;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean update(CreditWallet w) {
        String sql = "UPDATE credit_wallets SET total_balance = ?, lifetime_earned = ?, updated_at = ? WHERE wallet_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, w.getTotalBalance());
            ps.setInt(2, w.getLifetimeEarned());
            ps.setTimestamp(3, toTimestamp(w.getUpdatedAt()));
            ps.setString(4, w.getWalletId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean deleteById(String id) {
        String sql = "DELETE FROM credit_wallets WHERE wallet_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(ps, conn);
        }
    }

    @Override
    public Optional<CreditWallet> findByStudentId(String studentId) {
        String sql = "SELECT wallet_id, student_id, total_balance, lifetime_earned, updated_at FROM credit_wallets WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, studentId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapWallet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs, ps, conn);
        }
        return Optional.empty();
    }

    @Override
    public CreditTransaction recordTransaction(CreditTransaction tx) {
        Connection conn = null;
        PreparedStatement psTx = null;
        PreparedStatement psWallet = null;
        ResultSet gk = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Insert transaction
            String sqlTx = "INSERT INTO credit_transactions (wallet_id, complaint_id, transaction_type, credits, description, timestamp) VALUES (?, ?, ?, ?, ?, ?)";
            psTx = conn.prepareStatement(sqlTx, Statement.RETURN_GENERATED_KEYS);
            psTx.setString(1, tx.getWalletId());
            if (tx.getComplaintId() != null) psTx.setString(2, tx.getComplaintId());
            else psTx.setNull(2, Types.VARCHAR);
            psTx.setString(3, tx.getTransactionType().name());
            psTx.setInt(4, tx.getCredits());
            psTx.setString(5, tx.getDescription());
            psTx.setTimestamp(6, toTimestamp(tx.getTimestamp()));
            psTx.executeUpdate();

            gk = psTx.getGeneratedKeys();
            if (gk.next()) {
                tx.setTransactionId(gk.getInt(1));
            }

            // 2. Update wallet balance
            String sqlWallet = "UPDATE credit_wallets SET total_balance = total_balance + ?, lifetime_earned = lifetime_earned + ?, updated_at = NOW() WHERE wallet_id = ?";
            psWallet = conn.prepareStatement(sqlWallet);
            psWallet.setInt(1, tx.getCredits());
            psWallet.setInt(2, tx.getCredits());
            psWallet.setString(3, tx.getWalletId());
            psWallet.executeUpdate();

            conn.commit(); // Commit transaction
            return tx;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {}
            }
            e.printStackTrace();
            return null;
        } finally {
            closeQuietly(gk, psTx, psWallet, conn);
        }
    }

    @Override
    public List<CreditTransaction> findTransactionsByWalletId(String walletId) {
        List<CreditTransaction> list = new ArrayList<>();
        String sql = "SELECT transaction_id, wallet_id, complaint_id, transaction_type, credits, description, timestamp FROM credit_transactions WHERE wallet_id = ? ORDER BY timestamp DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, walletId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new CreditTransaction(
                        rs.getInt("transaction_id"),
                        rs.getString("wallet_id"),
                        rs.getString("complaint_id"),
                        TransactionType.valueOf(rs.getString("transaction_type")),
                        rs.getInt("credits"),
                        rs.getString("description"),
                        toLocalDateTime(rs.getTimestamp("timestamp"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs, ps, conn);
        }
        return list;
    }

    @Override
    public boolean hasTransactionForComplaint(String walletId, String complaintId, TransactionType type) {
        String sql = "SELECT COUNT(*) FROM credit_transactions WHERE wallet_id = ? AND complaint_id = ? AND transaction_type = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, walletId);
            ps.setString(2, complaintId);
            ps.setString(3, type.name());
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs, ps, conn);
        }
        return false;
    }

    @Override
    public List<CreditWallet> getLeaderboard(int limit) {
        List<CreditWallet> list = new ArrayList<>();
        String sql = "SELECT wallet_id, student_id, total_balance, lifetime_earned, updated_at FROM credit_wallets ORDER BY lifetime_earned DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, limit > 0 ? limit : 10);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapWallet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs, ps, conn);
        }
        return list;
    }

    private CreditWallet mapWallet(ResultSet rs) throws SQLException {
        return new CreditWallet(
                rs.getString("wallet_id"),
                rs.getString("student_id"),
                rs.getInt("total_balance"),
                rs.getInt("lifetime_earned"),
                toLocalDateTime(rs.getTimestamp("updated_at"))
        );
    }
}
