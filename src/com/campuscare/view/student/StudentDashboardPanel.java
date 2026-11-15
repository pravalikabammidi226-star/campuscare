package com.campuscare.view.student;

import com.campuscare.controller.AppController;
import com.campuscare.model.Complaint;
import com.campuscare.model.CreditWallet;
import com.campuscare.model.Student;
import com.campuscare.model.enums.ComplaintStatus;
import com.campuscare.util.UITheme;
import com.campuscare.view.admin.StudentLeaderboardPanel;
import com.campuscare.view.components.ModernButton;
import com.campuscare.view.components.ModernCard;
import com.campuscare.view.components.NotificationPopupDialog;
import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * Main dashboard container for authenticated students.
 */
public class StudentDashboardPanel extends JPanel {

    private final Frame parentFrame;
    private final Student student;

    private JLabel statActiveLabel;
    private JLabel statResolvedLabel;
    private JLabel statCreditsLabel;
    private JLabel statTierLabel;
    private ModernButton notifBtn;

    private StudentComplaintHistoryPanel historyPanel;
    private CreditWalletPanel walletPanel;
    private StudentLeaderboardPanel leaderboardPanel;
    private JTabbedPane tabbedPane;

    public StudentDashboardPanel(Frame parentFrame, Student student) {
        this.parentFrame = parentFrame;
        this.student = student;

        setLayout(new BorderLayout(0, 10));
        setBackground(UITheme.BG_MAIN);

        initUI();
        refreshAll();
    }

    private void initUI() {
        // Top Navigation Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.PRIMARY_DARK);
        topBar.setBorder(UITheme.createPadding(14, 20, 14, 20));

        JPanel userBox = new JPanel(new GridLayout(2, 1, 2, 2));
        userBox.setOpaque(false);
        JLabel brandLabel = new JLabel("CampusCare+  |  Student Portal");
        brandLabel.setFont(UITheme.FONT_TITLE);
        brandLabel.setForeground(Color.WHITE);

        JLabel studentLabel = new JLabel(student.getName() + " (" + student.getRollNumber() + ") • " + student.getDepartment() + " • Sem " + student.getSemester());
        studentLabel.setFont(UITheme.FONT_SMALL);
        studentLabel.setForeground(new Color(0x94, 0xA3, 0xB8));

        userBox.add(brandLabel);
        userBox.add(studentLabel);
        topBar.add(userBox, BorderLayout.WEST);

        // Right Actions (Submit, Notification, Logout)
        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBox.setOpaque(false);

        ModernButton submitBtn = new ModernButton("+ Submit Grievance", UITheme.PRIMARY_ACCENT, Color.WHITE);
        submitBtn.addActionListener(e -> {
            SubmitComplaintDialog dlg = new SubmitComplaintDialog(parentFrame, student.getId(), this::refreshAll);
            dlg.setVisible(true);
        });

        notifBtn = ModernButton.secondary("🔔 Notifications (0)");
        notifBtn.addActionListener(e -> {
            NotificationPopupDialog dlg = new NotificationPopupDialog(parentFrame, student.getId(),
                    AppController.getInstance().getNotificationService(), this::updateNotificationBadge);
            dlg.setVisible(true);
        });

        ModernButton logoutBtn = ModernButton.danger("Logout");
        logoutBtn.addActionListener(e -> AppController.getInstance().logout());

        actionBox.add(submitBtn);
        actionBox.add(notifBtn);
        actionBox.add(logoutBtn);
        topBar.add(actionBox, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Center Content Container
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.setBorder(UITheme.createPadding(10, 16, 12, 16));

        // KPI Summary Bar
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 12, 0));
        kpiRow.setOpaque(false);

        kpiRow.add(createKpiCard("ACTIVE GRIEVANCES", statActiveLabel = new JLabel("0"), UITheme.COLOR_IN_PROGRESS));
        kpiRow.add(createKpiCard("RESOLVED TICKETS", statResolvedLabel = new JLabel("0"), UITheme.COLOR_RESOLVED));
        kpiRow.add(createKpiCard("CREDIT WALLET", statCreditsLabel = new JLabel("0 pts"), UITheme.PRIMARY_ACCENT));
        kpiRow.add(createKpiCard("HONOR BADGE TIER", statTierLabel = new JLabel("Beginner"), UITheme.TEXT_PRIMARY));

        center.add(kpiRow, BorderLayout.NORTH);

        // Tabbed View
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BOLD);

        historyPanel = new StudentComplaintHistoryPanel(parentFrame, student.getId());
        walletPanel = new CreditWalletPanel(student.getId());
        leaderboardPanel = new StudentLeaderboardPanel();

        tabbedPane.addTab("  My Grievances & Status  ", historyPanel);
        tabbedPane.addTab("  CampusCare Credit Wallet  ", walletPanel);
        tabbedPane.addTab("  Campus Citizen Leaderboard  ", leaderboardPanel);

        center.add(tabbedPane, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color valueColor) {
        JPanel card = new ModernCard(new BorderLayout(4, 4));
        card.setBorder(UITheme.createPadding(10, 14, 10, 14));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL);
        titleLbl.setForeground(UITheme.TEXT_MUTED);

        valueLabel.setFont(UITheme.FONT_HEADER);
        valueLabel.setForeground(valueColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void refreshAll() {
        // KPI update
        List<Complaint> list = AppController.getInstance().getComplaintService().getComplaintsByStudent(student.getId());
        int active = (int) list.stream().filter(c -> !c.getStatus().isTerminal()).count();
        int resolved = (int) list.stream().filter(c -> c.getStatus() == ComplaintStatus.RESOLVED || c.getStatus() == ComplaintStatus.CLOSED).count();

        statActiveLabel.setText(String.valueOf(active));
        statResolvedLabel.setText(String.valueOf(resolved));

        CreditWallet wallet = AppController.getInstance().getCreditWalletService().getWalletByStudentId(student.getId());
        statCreditsLabel.setText(wallet.getTotalBalance() + " pts");
        statTierLabel.setText(wallet.getTierBadge());

        updateNotificationBadge();

        if (historyPanel != null) historyPanel.refreshData();
        if (walletPanel != null) walletPanel.refreshWallet();
        if (leaderboardPanel != null) leaderboardPanel.refreshLeaderboard();
    }

    private void updateNotificationBadge() {
        int unread = AppController.getInstance().getNotificationService().getUnreadCount(student.getId());
        if (unread > 0) {
            notifBtn.setText("🔔 Notifications (" + unread + ")");
            notifBtn.setForeground(UITheme.PRIORITY_URGENT);
        } else {
            notifBtn.setText("🔔 Notifications (0)");
            notifBtn.setForeground(UITheme.TEXT_PRIMARY);
        }
    }
}
