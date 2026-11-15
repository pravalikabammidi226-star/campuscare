package com.campuscare.view.admin;

import com.campuscare.controller.AppController;
import com.campuscare.model.Admin;
import com.campuscare.service.AnalyticsService;
import com.campuscare.util.UITheme;
import com.campuscare.view.components.ModernButton;
import com.campuscare.view.components.ModernCard;
import com.campuscare.view.components.NotificationPopupDialog;
import java.awt.*;
import javax.swing.*;

/**
 * Main dashboard container for campus administrators.
 */
public class AdminDashboardPanel extends JPanel {

    private final Frame parentFrame;
    private final Admin admin;

    private JLabel statTotalLabel;
    private JLabel statPendingLabel;
    private JLabel statResolvedLabel;
    private JLabel statEscalatedLabel;
    private ModernButton notifBtn;

    private AdminComplaintManagementPanel managementPanel;
    private CampusHeatmapPanel heatmapPanel;
    private AnalyticsReportPanel reportPanel;
    private StudentLeaderboardPanel leaderboardPanel;
    private JTabbedPane tabbedPane;

    public AdminDashboardPanel(Frame parentFrame, Admin admin) {
        this.parentFrame = parentFrame;
        this.admin = admin;

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
        JLabel brandLabel = new JLabel("CampusCare+  |  Administration Portal");
        brandLabel.setFont(UITheme.FONT_TITLE);
        brandLabel.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel(admin.getName() + " • " + admin.getRoleTitle() + " • Background SLA Monitor Running");
        roleLabel.setFont(UITheme.FONT_SMALL);
        roleLabel.setForeground(new Color(0x34, 0xD3, 0x99)); // Emerald indicator

        userBox.add(brandLabel);
        userBox.add(roleLabel);
        topBar.add(userBox, BorderLayout.WEST);

        // Right Actions
        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBox.setOpaque(false);

        notifBtn = ModernButton.secondary("🔔 Notifications (0)");
        notifBtn.addActionListener(e -> {
            NotificationPopupDialog dlg = new NotificationPopupDialog(parentFrame, admin.getId(),
                    AppController.getInstance().getNotificationService(), this::updateNotificationBadge);
            dlg.setVisible(true);
        });

        ModernButton logoutBtn = ModernButton.danger("Logout");
        logoutBtn.addActionListener(e -> AppController.getInstance().logout());

        actionBox.add(notifBtn);
        actionBox.add(logoutBtn);
        topBar.add(actionBox, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Center Content Container
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.setBorder(UITheme.createPadding(10, 16, 12, 16));

        // 4 KPI Cards
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 12, 0));
        kpiRow.setOpaque(false);

        kpiRow.add(createKpiCard("TOTAL REGISTERED", statTotalLabel = new JLabel("0"), UITheme.PRIMARY_ACCENT));
        kpiRow.add(createKpiCard("PENDING / IN ACTION", statPendingLabel = new JLabel("0"), UITheme.COLOR_IN_PROGRESS));
        kpiRow.add(createKpiCard("SUCCESSFULLY RESOLVED", statResolvedLabel = new JLabel("0"), UITheme.COLOR_RESOLVED));
        kpiRow.add(createKpiCard("SLA BREACH ESCALATIONS", statEscalatedLabel = new JLabel("0"), UITheme.PRIORITY_URGENT));

        center.add(kpiRow, BorderLayout.NORTH);

        // Tabbed View
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BOLD);

        managementPanel = new AdminComplaintManagementPanel(parentFrame, admin);
        heatmapPanel = new CampusHeatmapPanel();
        reportPanel = new AnalyticsReportPanel(parentFrame);
        leaderboardPanel = new StudentLeaderboardPanel();

        tabbedPane.addTab("  Master Grievance Desk  ", managementPanel);
        tabbedPane.addTab("  Campus Problem Heatmap  ", heatmapPanel);
        tabbedPane.addTab("  Department KPIs & Reports  ", reportPanel);
        tabbedPane.addTab("  Student Honor Leaderboard  ", leaderboardPanel);

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
        AnalyticsService.OverallStats stats = AppController.getInstance().getAnalyticsService().getOverallStats();
        statTotalLabel.setText(String.valueOf(stats.getTotalComplaints()));
        statPendingLabel.setText(String.valueOf(stats.getPendingComplaints()));
        statResolvedLabel.setText(String.valueOf(stats.getResolvedComplaints()));
        statEscalatedLabel.setText(String.valueOf(stats.getEscalatedComplaints()));

        updateNotificationBadge();

        if (managementPanel != null) managementPanel.refreshComplaints();
        if (heatmapPanel != null) heatmapPanel.refreshHeatmap();
        if (reportPanel != null) reportPanel.refreshMetrics();
        if (leaderboardPanel != null) leaderboardPanel.refreshLeaderboard();
    }

    private void updateNotificationBadge() {
        int unread = AppController.getInstance().getNotificationService().getUnreadCount(admin.getId());
        if (unread > 0) {
            notifBtn.setText("🔔 Notifications (" + unread + ")");
            notifBtn.setForeground(UITheme.PRIORITY_URGENT);
        } else {
            notifBtn.setText("🔔 Notifications (0)");
            notifBtn.setForeground(UITheme.TEXT_PRIMARY);
        }
    }
}
