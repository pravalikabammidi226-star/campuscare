package com.campuscare.view.admin;

import com.campuscare.controller.AppController;
import com.campuscare.service.CreditWalletService;
import com.campuscare.util.UITheme;
import com.campuscare.view.components.ModernButton;
import com.campuscare.view.components.ModernCard;
import com.campuscare.view.components.ModernTable;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Panel rendering the campus-wide student credit leaderboard and recognition rankings.
 */
public class StudentLeaderboardPanel extends JPanel {

    private ModernTable table;
    private DefaultTableModel tableModel;

    public StudentLeaderboardPanel() {
        setLayout(new BorderLayout(14, 14));
        setBackground(UITheme.BG_MAIN);
        setBorder(UITheme.createPadding(14, 16, 14, 16));

        initUI();
        refreshLeaderboard();
    }

    private void initUI() {
        JPanel card = new ModernCard(new BorderLayout(12, 12));
        card.setBorder(UITheme.createPadding(14, 18, 14, 18));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("🏆 CampusCare Student Honor Leaderboard");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Recognizing proactive student citizens for campus maintenance and safety contributions.");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_SECONDARY);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 2, 2));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(sub);

        ModernButton refreshBtn = ModernButton.secondary("↻ Refresh");
        refreshBtn.addActionListener(e -> refreshLeaderboard());

        header.add(titleBox, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        String[] cols = {"Rank", "Student Name", "Roll Number", "Department", "Current Balance", "Lifetime Points", "Honor Tier"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new ModernTable(tableModel);

        // Center align Rank and Points
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        card.add(scroll, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    public void refreshLeaderboard() {
        tableModel.setRowCount(0);
        List<CreditWalletService.LeaderboardEntry> entries = AppController.getInstance().getCreditWalletService().getLeaderboard(20);

        for (CreditWalletService.LeaderboardEntry e : entries) {
            String rankDisplay = "#" + e.getRank();
            if (e.getRank() == 1) rankDisplay = "🥇 #1";
            else if (e.getRank() == 2) rankDisplay = "🥈 #2";
            else if (e.getRank() == 3) rankDisplay = "🥉 #3";

            tableModel.addRow(new Object[]{
                    rankDisplay,
                    e.getStudentName(),
                    e.getRollNumber(),
                    e.getDepartment(),
                    e.getBalance() + " pts",
                    e.getLifetimeCredits() + " pts",
                    e.getTierBadge()
            });
        }
    }
}
