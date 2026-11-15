package com.campuscare.view.staff;

import com.campuscare.controller.AppController;
import com.campuscare.model.Complaint;
import com.campuscare.model.ComplaintHistory;
import com.campuscare.model.DepartmentStaff;
import com.campuscare.model.enums.ComplaintStatus;
import com.campuscare.util.DateTimeUtil;
import com.campuscare.util.UITheme;
import com.campuscare.view.components.ModernButton;
import com.campuscare.view.components.ModernCard;
import com.campuscare.view.components.ModernTable;
import com.campuscare.view.components.PriorityBadgeRenderer;
import com.campuscare.view.components.StatusBadgeRenderer;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel for department staff members to manage their assigned grievance tasks.
 */
public class AssignedComplaintsPanel extends JPanel {

    private final Frame parentFrame;
    private final DepartmentStaff staff;

    private JComboBox<String> statusFilter;
    private ModernTable table;
    private DefaultTableModel tableModel;
    private List<Complaint> currentList;
    private Complaint selectedComplaint;

    private ModernButton progressBtn;
    private ModernButton resolveBtn;
    private ModernButton rejectBtn;
    private ModernButton historyBtn;

    public AssignedComplaintsPanel(Frame parentFrame, DepartmentStaff staff) {
        this.parentFrame = parentFrame;
        this.staff = staff;

        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG_MAIN);
        setBorder(UITheme.createPadding(14, 16, 14, 16));

        initUI();
        refreshComplaints();
    }

    private void initUI() {
        // Top Filter Bar
        JPanel filterCard = new ModernCard(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filterCard.setBorder(UITheme.createPadding(10, 14, 10, 14));

        statusFilter = new JComboBox<>();
        statusFilter.addItem("All Assigned Tasks");
        statusFilter.addItem(ComplaintStatus.ASSIGNED.name());
        statusFilter.addItem(ComplaintStatus.IN_PROGRESS.name());
        statusFilter.addItem(ComplaintStatus.RESOLVED.name());
        statusFilter.addItem(ComplaintStatus.ESCALATED.name());
        statusFilter.setFont(UITheme.FONT_BODY);
        statusFilter.addActionListener(e -> refreshComplaints());

        ModernButton refreshBtn = ModernButton.secondary("↻ Refresh List");
        refreshBtn.addActionListener(e -> refreshComplaints());

        filterCard.add(new JLabel("Task Status Filter:"));
        filterCard.add(statusFilter);
        filterCard.add(refreshBtn);

        add(filterCard, BorderLayout.NORTH);

        // Center Table Card
        JPanel tableCard = new ModernCard(new BorderLayout(10, 10));
        tableCard.setBorder(UITheme.createPadding(12, 14, 12, 14));

        String[] cols = {"Ticket #", "Student", "Category", "Title", "Location", "Priority", "Status", "Resolution SLA Deadline"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new ModernTable(tableModel);
        table.getColumnModel().getColumn(5).setCellRenderer(new PriorityBadgeRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0 && row < currentList.size()) {
                    selectedComplaint = currentList.get(row);
                    updateActionButtons(selectedComplaint);
                } else {
                    selectedComplaint = null;
                    updateActionButtons(null);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        tableCard.add(scroll, BorderLayout.CENTER);

        // Action Toolbar
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        actionToolbar.setOpaque(false);

        historyBtn = ModernButton.secondary("View Audit Timeline");
        historyBtn.setEnabled(false);
        historyBtn.addActionListener(e -> showHistoryDialog());

        progressBtn = new ModernButton("Update Progress Notes");
        progressBtn.setEnabled(false);
        progressBtn.addActionListener(e -> showProgressDialog());

        resolveBtn = ModernButton.success("Mark as Resolved");
        resolveBtn.setEnabled(false);
        resolveBtn.addActionListener(e -> handleResolve());

        rejectBtn = ModernButton.danger("Reject / Return");
        rejectBtn.setEnabled(false);
        rejectBtn.addActionListener(e -> handleReject());

        actionToolbar.add(historyBtn);
        actionToolbar.add(progressBtn);
        actionToolbar.add(resolveBtn);
        actionToolbar.add(rejectBtn);

        tableCard.add(actionToolbar, BorderLayout.SOUTH);
        add(tableCard, BorderLayout.CENTER);
    }

    private void updateActionButtons(Complaint c) {
        if (c == null) {
            progressBtn.setEnabled(false);
            resolveBtn.setEnabled(false);
            rejectBtn.setEnabled(false);
            historyBtn.setEnabled(false);
            return;
        }

        historyBtn.setEnabled(true);
        boolean terminal = c.getStatus().isTerminal();
        progressBtn.setEnabled(!terminal);
        resolveBtn.setEnabled(!terminal);
        rejectBtn.setEnabled(!terminal);
    }

    public void refreshComplaints() {
        // Query complaints assigned to this staff or to their department
        List<Complaint> allStaffComplaints = AppController.getInstance().getComplaintService().getComplaintsByStaff(staff.getId());
        if (allStaffComplaints.isEmpty()) {
            // Also show department unassigned tickets
            allStaffComplaints = AppController.getInstance().getComplaintService().getComplaintsByDepartment(staff.getDepartmentId());
        }

        String filter = (String) statusFilter.getSelectedItem();
        if (filter != null && !filter.startsWith("All")) {
            ComplaintStatus st = ComplaintStatus.valueOf(filter);
            currentList = allStaffComplaints.stream().filter(c -> c.getStatus() == st).collect(Collectors.toList());
        } else {
            currentList = allStaffComplaints;
        }

        tableModel.setRowCount(0);
        for (Complaint c : currentList) {
            tableModel.addRow(new Object[]{
                    c.getTicketNumber(),
                    c.getDisplayAuthor(false), // mask if anonymous
                    c.getCategory().getDisplayName(),
                    c.getTitle(),
                    c.getLocation(),
                    c.getPriority(),
                    c.getStatus(),
                    DateTimeUtil.formatDisplay(c.getDeadlineAt())
            });
        }
        updateActionButtons(null);
    }

    private void showProgressDialog() {
        if (selectedComplaint == null) return;
        UpdateProgressDialog dlg = new UpdateProgressDialog(parentFrame, selectedComplaint, staff.getName(), this::refreshComplaints);
        dlg.setVisible(true);
    }

    private void handleResolve() {
        if (selectedComplaint == null) return;
        String note = JOptionPane.showInputDialog(this,
                "Enter resolution description & work completed for #" + selectedComplaint.getTicketNumber() + ":",
                "Complete & Resolve Task", JOptionPane.PLAIN_MESSAGE);

        if (note != null && !note.trim().isEmpty()) {
            try {
                AppController.getInstance().getComplaintService().resolveComplaint(
                        selectedComplaint.getComplaintId(),
                        staff.getName(),
                        "STAFF",
                        note.trim()
                );
                JOptionPane.showMessageDialog(this, "Ticket #" + selectedComplaint.getTicketNumber() + " successfully marked as RESOLVED.");
                refreshComplaints();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleReject() {
        if (selectedComplaint == null) return;
        String reason = JOptionPane.showInputDialog(this,
                "State reason for rejecting/returning ticket #" + selectedComplaint.getTicketNumber() + ":",
                "Reject Task", JOptionPane.WARNING_MESSAGE);

        if (reason != null && !reason.trim().isEmpty()) {
            try {
                AppController.getInstance().getComplaintService().rejectComplaint(
                        selectedComplaint.getComplaintId(),
                        staff.getName(),
                        "STAFF",
                        reason.trim()
                );
                JOptionPane.showMessageDialog(this, "Task rejected and returned.");
                refreshComplaints();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showHistoryDialog() {
        if (selectedComplaint == null) return;
        List<ComplaintHistory> historyList = AppController.getInstance().getComplaintService().getComplaintHistory(selectedComplaint.getComplaintId());

        StringBuilder sb = new StringBuilder();
        sb.append("TICKET #").append(selectedComplaint.getTicketNumber()).append(" - ").append(selectedComplaint.getTitle()).append("\n\n");
        sb.append("Location: ").append(selectedComplaint.getLocation()).append("\n");
        sb.append("Reported By: ").append(selectedComplaint.getDisplayAuthor(false)).append("\n\n");
        sb.append("--- AUDIT TIMELINE ---\n");

        for (ComplaintHistory h : historyList) {
            sb.append(String.format("[%s] Action: %s -> %s%n  By: %s (%s)%n  Remarks: %s%n%n",
                    DateTimeUtil.formatDisplay(h.getTimestamp()),
                    h.getStatusFrom() != null ? h.getStatusFrom() : "INITIAL",
                    h.getStatusTo(),
                    h.getChangedByName(),
                    h.getChangedByRole(),
                    h.getRemarks()
            ));
        }

        JTextArea area = new JTextArea(sb.toString(), 18, 50);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);

        JOptionPane.showMessageDialog(this, scroll, "Timeline Audit: " + selectedComplaint.getTicketNumber(), JOptionPane.INFORMATION_MESSAGE);
    }
}
