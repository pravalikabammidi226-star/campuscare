package com.campuscare.view.admin;

import com.campuscare.controller.AppController;
import com.campuscare.model.Admin;
import com.campuscare.model.Complaint;
import com.campuscare.model.ComplaintHistory;
import com.campuscare.model.enums.ComplaintCategory;
import com.campuscare.model.enums.ComplaintStatus;
import com.campuscare.model.enums.PriorityLevel;
import com.campuscare.util.DateTimeUtil;
import com.campuscare.util.UITheme;
import com.campuscare.view.components.ModernButton;
import com.campuscare.view.components.ModernCard;
import com.campuscare.view.components.ModernTable;
import com.campuscare.view.components.PriorityBadgeRenderer;
import com.campuscare.view.components.StatusBadgeRenderer;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel for Campus Administrators to filter, inspect, assign, resolve, and reject complaints.
 */
public class AdminComplaintManagementPanel extends JPanel {

    private final Frame parentFrame;
    private final Admin admin;

    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> categoryFilterCombo;

    private ModernTable table;
    private DefaultTableModel tableModel;
    private List<Complaint> currentList;
    private Complaint selectedComplaint;

    private ModernButton assignBtn;
    private ModernButton resolveBtn;
    private ModernButton rejectBtn;
    private ModernButton historyBtn;

    public AdminComplaintManagementPanel(Frame parentFrame, Admin admin) {
        this.parentFrame = parentFrame;
        this.admin = admin;

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

        searchField = new JTextField(14);
        searchField.setFont(UITheme.FONT_BODY);
        searchField.setToolTipText("Search keyword, ticket, location...");

        statusFilterCombo = new JComboBox<>();
        statusFilterCombo.addItem("All Statuses");
        for (ComplaintStatus s : ComplaintStatus.values()) {
            statusFilterCombo.addItem(s.name());
        }
        statusFilterCombo.setFont(UITheme.FONT_BODY);

        categoryFilterCombo = new JComboBox<>();
        categoryFilterCombo.addItem("All Categories");
        for (ComplaintCategory c : ComplaintCategory.values()) {
            categoryFilterCombo.addItem(c.name());
        }
        categoryFilterCombo.setFont(UITheme.FONT_BODY);

        ModernButton filterBtn = new ModernButton("Filter / Search");
        filterBtn.addActionListener(e -> refreshComplaints());

        ModernButton clearBtn = ModernButton.secondary("Clear");
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            statusFilterCombo.setSelectedIndex(0);
            categoryFilterCombo.setSelectedIndex(0);
            refreshComplaints();
        });

        filterCard.add(new JLabel("Keyword:"));
        filterCard.add(searchField);
        filterCard.add(new JLabel("Status:"));
        filterCard.add(statusFilterCombo);
        filterCard.add(new JLabel("Category:"));
        filterCard.add(categoryFilterCombo);
        filterCard.add(filterBtn);
        filterCard.add(clearBtn);

        add(filterCard, BorderLayout.NORTH);

        // Center Table Card
        JPanel tableCard = new ModernCard(new BorderLayout(10, 10));
        tableCard.setBorder(UITheme.createPadding(12, 14, 12, 14));

        String[] cols = {"Ticket #", "Student", "Category", "Title", "Location", "Priority", "Status", "Department", "Staff Officer", "Deadline"};
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

        historyBtn = ModernButton.secondary("Inspect Audit Timeline");
        historyBtn.setEnabled(false);
        historyBtn.addActionListener(e -> showHistoryDialog());

        assignBtn = new ModernButton("Route & Assign Ticket");
        assignBtn.setEnabled(false);
        assignBtn.addActionListener(e -> showAssignDialog());

        resolveBtn = ModernButton.success("Mark Resolved");
        resolveBtn.setEnabled(false);
        resolveBtn.addActionListener(e -> handleResolve());

        rejectBtn = ModernButton.danger("Decline / Reject");
        rejectBtn.setEnabled(false);
        rejectBtn.addActionListener(e -> handleReject());

        actionToolbar.add(historyBtn);
        actionToolbar.add(assignBtn);
        actionToolbar.add(resolveBtn);
        actionToolbar.add(rejectBtn);

        tableCard.add(actionToolbar, BorderLayout.SOUTH);
        add(tableCard, BorderLayout.CENTER);
    }

    private void updateActionButtons(Complaint c) {
        if (c == null) {
            assignBtn.setEnabled(false);
            resolveBtn.setEnabled(false);
            rejectBtn.setEnabled(false);
            historyBtn.setEnabled(false);
            return;
        }

        historyBtn.setEnabled(true);
        boolean terminal = c.getStatus().isTerminal();
        assignBtn.setEnabled(!terminal);
        resolveBtn.setEnabled(!terminal);
        rejectBtn.setEnabled(!terminal);
    }

    public void refreshComplaints() {
        String kw = searchField.getText().trim();
        String selStatus = (String) statusFilterCombo.getSelectedItem();
        String selCat = (String) categoryFilterCombo.getSelectedItem();

        ComplaintStatus status = (selStatus != null && !selStatus.startsWith("All"))
                ? ComplaintStatus.valueOf(selStatus) : null;
        ComplaintCategory category = (selCat != null && !selCat.startsWith("All"))
                ? ComplaintCategory.valueOf(selCat) : null;

        currentList = AppController.getInstance().getComplaintService().filterComplaints(kw, status, category);

        tableModel.setRowCount(0);
        for (Complaint c : currentList) {
            tableModel.addRow(new Object[]{
                    c.getTicketNumber(),
                    c.getDisplayAuthor(true),
                    c.getCategory().getDisplayName(),
                    c.getTitle(),
                    c.getLocation(),
                    c.getPriority(),
                    c.getStatus(),
                    c.getAssignedDepartmentName() != null ? c.getAssignedDepartmentName() : "Unassigned",
                    c.getAssignedStaffName() != null ? c.getAssignedStaffName() : "None",
                    DateTimeUtil.formatDateOnly(c.getDeadlineAt())
            });
        }
        updateActionButtons(null);
    }

    private void showAssignDialog() {
        if (selectedComplaint == null) return;
        AssignDepartmentDialog dlg = new AssignDepartmentDialog(parentFrame, selectedComplaint, admin.getName(), this::refreshComplaints);
        dlg.setVisible(true);
    }

    private void handleResolve() {
        if (selectedComplaint == null) return;
        String note = JOptionPane.showInputDialog(this,
                "Enter resolution summary for ticket #" + selectedComplaint.getTicketNumber() + ":",
                "Resolve Complaint", JOptionPane.PLAIN_MESSAGE);

        if (note != null && !note.trim().isEmpty()) {
            try {
                AppController.getInstance().getComplaintService().resolveComplaint(
                        selectedComplaint.getComplaintId(),
                        admin.getName(),
                        "ADMIN",
                        note.trim()
                );
                JOptionPane.showMessageDialog(this, "Ticket #" + selectedComplaint.getTicketNumber() + " marked as RESOLVED.");
                refreshComplaints();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleReject() {
        if (selectedComplaint == null) return;
        String reason = JOptionPane.showInputDialog(this,
                "Provide formal justification for declining ticket #" + selectedComplaint.getTicketNumber() + ":",
                "Decline Grievance", JOptionPane.WARNING_MESSAGE);

        if (reason != null && !reason.trim().isEmpty()) {
            try {
                AppController.getInstance().getComplaintService().rejectComplaint(
                        selectedComplaint.getComplaintId(),
                        admin.getName(),
                        "ADMIN",
                        reason.trim()
                );
                JOptionPane.showMessageDialog(this, "Ticket declined.");
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
        sb.append("Student: ").append(selectedComplaint.getDisplayAuthor(true)).append("\n\n");
        sb.append("--- AUDIT TIMELINE (FULL TRANSPARENCY) ---\n");

        for (ComplaintHistory h : historyList) {
            sb.append(String.format("[%s] State: %s -> %s%n  By: %s (%s)%n  Remarks: %s%n%n",
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

        JOptionPane.showMessageDialog(this, scroll, "Transparency Audit Log: " + selectedComplaint.getTicketNumber(), JOptionPane.INFORMATION_MESSAGE);
    }
}
