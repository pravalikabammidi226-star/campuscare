package com.campuscare.view.student;

import com.campuscare.controller.AppController;
import com.campuscare.model.CreditTransaction;
import com.campuscare.model.CreditWallet;
import com.campuscare.util.DateTimeUtil;
import com.campuscare.util.UITheme;
import com.campuscare.view.components.ModernCard;
import com.campuscare.view.components.ModernTable;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Panel displaying the student's CampusCare Credit Wallet, tier badge, rules, and transaction ledger.
 */
public class CreditWalletPanel extends JPanel {

    private final String studentId;
    private JLabel balanceLabel;
    private JLabel lifetimeLabel;
    private JLabel tierLabel;
    private ModernTable transactionTable;
    private DefaultTableModel tableModel;

    public CreditWalletPanel(String studentId) {
        this.studentId = studentId;

        setLayout(new BorderLayout(14, 14));
        setBackground(UITheme.BG_MAIN);
        setBorder(UITheme.createPadding(14, 16, 14, 16));

        initUI();
        refreshWallet();
    }

    private void initUI() {
        // Top 3 Stat Cards
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 14, 0));
        statsRow.setOpaque(false);

        // 1. Current Balance
        JPanel card1 = new ModernCard(new BorderLayout(4, 4));
        card1.setBorder(UITheme.createPadding(14, 16, 14, 16));
        JLabel l1 = new JLabel("AVAILABLE CREDITS");
        l1.setFont(UITheme.FONT_SMALL);
        l1.setForeground(UITheme.TEXT_MUTED);
        balanceLabel = new JLabel("0 pts");
        balanceLabel.setFont(UITheme.FONT_STAT_NUMBER);
        balanceLabel.setForeground(UITheme.PRIMARY_ACCENT);
        card1.add(l1, BorderLayout.NORTH);
        card1.add(balanceLabel, BorderLayout.CENTER);

        // 2. Lifetime Earned
        JPanel card2 = new ModernCard(new BorderLayout(4, 4));
        card2.setBorder(UITheme.createPadding(14, 16, 14, 16));
        JLabel l2 = new JLabel("LIFETIME EARNED");
        l2.setFont(UITheme.FONT_SMALL);
        l2.setForeground(UITheme.TEXT_MUTED);
        lifetimeLabel = new JLabel("0 pts");
        lifetimeLabel.setFont(UITheme.FONT_STAT_NUMBER);
        lifetimeLabel.setForeground(UITheme.COLOR_RESOLVED);
        card2.add(l2, BorderLayout.NORTH);
        card2.add(lifetimeLabel, BorderLayout.CENTER);

        // 3. Honor Tier
        JPanel card3 = new ModernCard(new BorderLayout(4, 4));
        card3.setBorder(UITheme.createPadding(14, 16, 14, 16));
        JLabel l3 = new JLabel("CAMPUS CITIZEN TIER");
        l3.setFont(UITheme.FONT_SMALL);
        l3.setForeground(UITheme.TEXT_MUTED);
        tierLabel = new JLabel("Beginner Citizen");
        tierLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        tierLabel.setForeground(UITheme.COLOR_IN_PROGRESS);
        card3.add(l3, BorderLayout.NORTH);
        card3.add(tierLabel, BorderLayout.CENTER);

        statsRow.add(card1);
        statsRow.add(card2);
        statsRow.add(card3);
        add(statsRow, BorderLayout.NORTH);

        // Center Split: Rules / Ledger
        JPanel center = new JPanel(new BorderLayout(14, 14));
        center.setOpaque(false);

        // Rules Guide Banner
        JPanel rulesCard = new ModernCard(new BorderLayout(8, 6));
        rulesCard.setBorder(UITheme.createPadding(12, 16, 12, 16));
        JLabel rulesTitle = new JLabel("How You Earn CampusCare Credits (Participation Rewards):");
        rulesTitle.setFont(UITheme.FONT_BOLD);
        rulesTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel rulesText = new JLabel("<html>" +
                "• <b>+5 Credits</b>: Submitting a valid campus complaint &nbsp;|&nbsp; " +
                "• <b>+3 Credits</b>: Supplementary photo/details &nbsp;|&nbsp; " +
                "• <b>+2 Credits</b>: Rating resolution &amp; feedback<br>" +
                "• <b>+5 Bonus Credits</b>: Reporting verified hazard/safety issue &nbsp;|&nbsp; " +
                "• <b>+3 Credits</b>: Verifying completed maintenance repair<br>" +
                "<font color='#64748B'>* Note: Duplicate/spam tickets yield 0 credits. Credits represent merit participation points on the campus leaderboard.</font>" +
                "</html>");
        rulesText.setFont(UITheme.FONT_SMALL);
        rulesText.setForeground(UITheme.TEXT_SECONDARY);

        rulesCard.add(rulesTitle, BorderLayout.NORTH);
        rulesCard.add(rulesText, BorderLayout.CENTER);
        center.add(rulesCard, BorderLayout.NORTH);

        // Ledger Table
        JPanel ledgerCard = new ModernCard(new BorderLayout(10, 10));
        ledgerCard.setBorder(UITheme.createPadding(12, 16, 12, 16));

        JLabel ledgerTitle = new JLabel("Transaction History & Audit Ledger");
        ledgerTitle.setFont(UITheme.FONT_HEADER);
        ledgerTitle.setForeground(UITheme.TEXT_PRIMARY);
        ledgerCard.add(ledgerTitle, BorderLayout.NORTH);

        String[] cols = {"Transaction ID", "Date & Time", "Action / Category", "Credits", "Description"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        transactionTable = new ModernTable(tableModel);
        // Highlight credits in green
        transactionTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setFont(UITheme.FONT_BOLD);
                l.setForeground(UITheme.COLOR_RESOLVED);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(transactionTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        ledgerCard.add(scroll, BorderLayout.CENTER);

        center.add(ledgerCard, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    public void refreshWallet() {
        CreditWallet wallet = AppController.getInstance().getCreditWalletService().getWalletByStudentId(studentId);
        balanceLabel.setText(wallet.getTotalBalance() + " pts");
        lifetimeLabel.setText(wallet.getLifetimeEarned() + " pts");
        tierLabel.setText(wallet.getTierBadge());

        tableModel.setRowCount(0);
        List<CreditTransaction> txList = AppController.getInstance().getCreditWalletService().getTransactionHistory(studentId);
        for (CreditTransaction tx : txList) {
            tableModel.addRow(new Object[]{
                    "TX-" + tx.getTransactionId(),
                    DateTimeUtil.formatDisplay(tx.getTimestamp()),
                    tx.getTransactionType().getDescription(),
                    "+" + tx.getCredits(),
                    tx.getDescription()
            });
        }
    }
}
