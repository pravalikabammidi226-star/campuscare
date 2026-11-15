package com.campuscare.view.staff;

import com.campuscare.controller.AppController;
import com.campuscare.model.Complaint;
import com.campuscare.model.DepartmentStaff;
import com.campuscare.model.enums.ComplaintStatus;
import com.campuscare.util.UITheme;
import com.campuscare.view.components.ModernButton;
import com.campuscare.view.components.ModernCard;
import com.campuscare.view.components.NotificationPopupDialog;
import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * Main dashboard container for authenticated department staff members.
 */
public class StaffDashboardPanel extends JPanel {

    private final Frame parentFrame;
    private final DepartmentStaff staff;

    private JLabel statTotalLabel;
    private JLabel statInProgressLabel;
    private JLabel statResolvedLabel;
    private ModernButton notifBtn;
    private AssignedComplaintsPanel complaintsPanel;

    public StaffDashboardPanel(Frame parentFrame, DepartmentStaff staff) {
        this.parentFrame = parentFrame;
        this.staff = staff;

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
        JLabel brandLabel = new JLabel("CampusCare+  |  Department Staff Portal");
        brandLabel.setFont(UITheme.FONT_TITLE);
        brandLabel.setForeground(Color.WHITE);

        JLabel staffInfo = new JLabel(staff.getName() + " • " + (staff.getDepartmentName() != null ? staff.getDepartmentName() : "Department " + staff.getDepartmentId()) + " • Code: " + staff.getEmployeeCode());
        staffInfo.setFont(UITheme.FONT_SMALL);
        staffInfo.setForeground(new Color(0x94, 0xA3, 0xB8));

        userBox.add(brandLabel);
        userBox.add(staffInfo);
        topBar.add(userBox, BorderLayout.WEST);

        // Right Actions
        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBox.setOpaque(false);

        notifBtn = ModernButton.secondary("🔔 Notifications (0)");
        notifBtn.addActionListener(e -> {
            NotificationPopupDialog dlg = new NotificationPopupDialog(parentFrame, staff.getId(),
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

        // 3 KPI Cards
        JPanel kpiRow = new JPanel(new GridLayout(1, 3, 14, 0));
        kpiRow.setOpaque(false);

        kpiRow.add(createKpiCard("ASSIGNED TASKS", statTotalLabel = new JLabel("0"), UITheme.PRIMARY_ACCENT));
        kpiRow.add(createKpiCard("CURRENTLY IN PROGRESS", statInProgressLabel = new JLabel("0"), UITheme.COLOR_IN_PROGRESS));
        kpiRow.add(createKpiCard("RESOLVED TICKETS", statResolvedLabel = new JLabel("0"), UITheme.COLOR_RESOLVED));

        center.add(kpiRow, BorderLayout.NORTH);

        complaintsPanel = new AssignedComplaintsPanel(parentFrame, staff);
        center.add(complaintsPanel, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color valueColor) {
        JPanel card = new ModernCard(new BorderLayout(4, 4));
        card.setBorder(UITheme.createPadding(12, 16, 12, 16));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL);
        titleLbl.setForeground(UITheme.TEXT_MUTED);

        valueLabel.setFont(UITheme.FONT_STAT_NUMBER);
        valueLabel.setForeground(valueColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void refreshAll() {
        List<Complaint> list = AppController.getInstance().getComplaintService().getComplaintsByStaff(staff.getId());
        if (list.isEmpty()) {
            list = AppController.getInstance().getComplaintService().getComplaintsByDepartment(staff.getDepartmentId());
        }

        int total = list.size();
        int inProg = (int) list.stream().filter(c -> c.getStatus() == ComplaintStatus.IN_PROGRESS).count();
        int resolved = (int) list.stream().filter(c -> c.getStatus() == ComplaintStatus.RESOLVED || c.getStatus() == ComplaintStatus.CLOSED).count();

        statTotalLabel.setText(String.valueOf(total));
        statInProgressLabel.setText(String.valueOf(inProg));
        statResolvedLabel.setText(String.valueOf(resolved));

        updateNotificationBadge();
        if (complaintsPanel != null) complaintsPanel.refreshComplaints();
    }

    private void updateNotificationBadge() {
        int unread = AppController.getInstance().getNotificationService().getUnreadCount(staff.getId());
        if (unread > 0) {
            notifBtn.setText("🔔 Notifications (" + unread + ")");
            notifBtn.setForeground(UITheme.PRIORITY_URGENT);
        } else {
            notifBtn.setText("🔔 Notifications (0)");
            notifBtn.setForeground(UITheme.TEXT_PRIMARY);
        }
    }
}
