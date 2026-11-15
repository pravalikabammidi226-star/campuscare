package com.campuscare.view.student;

import com.campuscare.controller.AppController;
import com.campuscare.model.Complaint;
import com.campuscare.model.ComplaintHistory;
import com.campuscare.model.enums.ComplaintStatus;
import com.campuscare.model.enums.TransactionType;
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
 * Panel displaying student's complaint tracking table and full transparent timeline audit history.
 */
public class StudentComplaintHistoryPanel extends JPanel {

    private final String studentId;
    private final Frame parentFrame;
    private ModernTable table;
    private DefaultTableModel tableModel;
    private List<Complaint> studentComplaints;

    private JLabel detailTicketLabel;
    private JLabel detailStatusLabel;
    private JTextArea detailDescArea;
    private JTextArea timelineArea;
    private ModernButton feedbackBtn;
    private ModernButton addInfoBtn;

    private Complaint selectedComplaint;

    public StudentComplaintHistoryPanel(Frame parentFrame, String studentId) {
        this.parentFrame = parentFrame;
        this.studentId = studentId;

        setLayout(new BorderLayout(14, 14));
        setBackground(UITheme.BG_MAIN);
        setBorder(UITheme.createPadding(14, 16, 14, 16));

        initUI();
        refreshData();
    }

    private void initUI() {
        // SplitPane dividing Table and Details
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.55);
        splitPane.setBorder(null);

        // Top Table Card
        JPanel tableCard = new ModernCard(new BorderLayout(10, 10));
        tableCard.setBorder(UITheme.createPadding(12, 14, 12, 14));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        JLabel title = new JLabel("My Grievance Tracking & History");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        ModernButton refreshBtn = ModernButton.secondary("↻ Refresh");
        refreshBtn.addActionListener(e -> refreshData());

        tableHeader.add(title, BorderLayout.WEST);
        tableHeader.add(refreshBtn, BorderLayout.EAST);
        tableCard.add(tableHeader, BorderLayout.NORTH);

        String[] cols = {"Ticket #", "Category", "Title", "Location", "Priority", "Status", "Filed On", "Deadline"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new ModernTable(tableModel);
        table.getColumnModel().getColumn(4).setCellRenderer(new PriorityBadgeRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0 && row < studentComplaints.size()) {
                    selectComplaint(studentComplaints.get(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        splitPane.setTopComponent(tableCard);

        // Bottom Details & Timeline Card
        JPanel detailCard = new ModernCard(new BorderLayout(12, 10));
        detailCard.setBorder(UITheme.createPadding(12, 14, 12, 14));

        JPanel detailHeader = new JPanel(new BorderLayout());
        detailHeader.setOpaque(false);
        detailTicketLabel = new JLabel("Select a complaint to inspect resolution details and audit timeline.");
        detailTicketLabel.setFont(UITheme.FONT_SUBHEADER);
        detailTicketLabel.setForeground(UITheme.TEXT_PRIMARY);

        detailStatusLabel = new JLabel("");
        detailStatusLabel.setFont(UITheme.FONT_BOLD);

        detailHeader.add(detailTicketLabel, BorderLayout.WEST);
        detailHeader.add(detailStatusLabel, BorderLayout.EAST);
        detailCard.add(detailHeader, BorderLayout.NORTH);

        JPanel detailsGrid = new JPanel(new GridLayout(1, 2, 12, 0));
        detailsGrid.setOpaque(false);

        // Left: Complaint Description
        JPanel left = new JPanel(new BorderLayout(4, 4));
        left.setOpaque(false);
        JLabel dTitle = new JLabel("Complaint Description & Notes:");
        dTitle.setFont(UITheme.FONT_BOLD);
        dTitle.setForeground(UITheme.TEXT_SECONDARY);
        detailDescArea = new JTextArea();
        detailDescArea.setEditable(false);
        detailDescArea.setLineWrap(true);
        detailDescArea.setWrapStyleWord(true);
        detailDescArea.setFont(UITheme.FONT_BODY);
        JScrollPane descScroll = new JScrollPane(detailDescArea);
        descScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        left.add(dTitle, BorderLayout.NORTH);
        left.add(descScroll, BorderLayout.CENTER);

        // Right: Audit Timeline History
        JPanel right = new JPanel(new BorderLayout(4, 4));
        right.setOpaque(false);
        JLabel tTitle = new JLabel("Transparency Audit Timeline (Every Action Logged):");
        tTitle.setFont(UITheme.FONT_BOLD);
        tTitle.setForeground(UITheme.TEXT_SECONDARY);
        timelineArea = new JTextArea();
        timelineArea.setEditable(false);
        timelineArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        timelineArea.setLineWrap(true);
        timelineArea.setWrapStyleWord(true);
        JScrollPane timeScroll = new JScrollPane(timelineArea);
        timeScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        right.add(tTitle, BorderLayout.NORTH);
        right.add(timeScroll, BorderLayout.CENTER);

        detailsGrid.add(left);
        detailsGrid.add(right);
        detailCard.add(detailsGrid, BorderLayout.CENTER);

        // Bottom Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        addInfoBtn = ModernButton.secondary("Provide Supplementary Info (+3 Credits)");
        addInfoBtn.setEnabled(false);
        addInfoBtn.addActionListener(e -> handleAddInfo());

        feedbackBtn = ModernButton.success("★ Rate Resolution (+2 Credits)");
        feedbackBtn.setEnabled(false);
        feedbackBtn.addActionListener(e -> handleFeedback());

        actionPanel.add(addInfoBtn);
        actionPanel.add(feedbackBtn);
        detailCard.add(actionPanel, BorderLayout.SOUTH);

        splitPane.setBottomComponent(detailCard);
        add(splitPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        studentComplaints = AppController.getInstance().getComplaintService().getComplaintsByStudent(studentId);

        for (Complaint c : studentComplaints) {
            tableModel.addRow(new Object[]{
                    c.getTicketNumber(),
                    c.getCategory().getDisplayName(),
                    c.getTitle(),
                    c.getLocation(),
                    c.getPriority(),
                    c.getStatus(),
                    DateTimeUtil.formatDateOnly(c.getCreatedAt()),
                    DateTimeUtil.formatDateOnly(c.getDeadlineAt())
            });
        }

        if (selectedComplaint != null) {
            // Re-select if still present
            for (Complaint c : studentComplaints) {
                if (c.getComplaintId().equals(selectedComplaint.getComplaintId())) {
                    selectComplaint(c);
                    break;
                }
            }
        }
    }

    private void selectComplaint(Complaint c) {
        this.selectedComplaint = c;
        detailTicketLabel.setText("Ticket #" + c.getTicketNumber() + " - " + c.getTitle());
        detailStatusLabel.setText("Status: " + c.getStatus().getDisplayName());

        StringBuilder sb = new StringBuilder();
        sb.append("Location: ").append(c.getLocation()).append("\n");
        sb.append("Category: ").append(c.getCategory().getDisplayName()).append("\n");
        sb.append("Department: ").append(c.getAssignedDepartmentName() != null ? c.getAssignedDepartmentName() : "Awaiting assignment").append("\n");
        sb.append("Officer: ").append(c.getAssignedStaffName() != null ? c.getAssignedStaffName() : "None yet").append("\n");
        if (c.isAnonymous()) {
            sb.append("Reporting Mode: Anonymous (Confidential)\n");
        }
        sb.append("\nDetailed Description:\n").append(c.getDescription());
        if (c.getRejectionReason() != null) {
            sb.append("\n\nREJECTION NOTE:\n").append(c.getRejectionReason());
        }
        detailDescArea.setText(sb.toString());

        // Timeline History
        List<ComplaintHistory> historyList = AppController.getInstance().getComplaintService().getComplaintHistory(c.getComplaintId());
        StringBuilder th = new StringBuilder();
        for (ComplaintHistory h : historyList) {
            th.append(String.format("[%s] %s%n  Action: %s -> %s%n  Actor: %s (%s)%n  Remarks: %s%n%n",
                    DateTimeUtil.formatDisplay(h.getTimestamp()),
                    h.getStatusTo(),
                    h.getStatusFrom() != null ? h.getStatusFrom() : "START",
                    h.getStatusTo(),
                    h.getChangedByName(),
                    h.getChangedByRole(),
                    h.getRemarks()
            ));
        }
        timelineArea.setText(th.toString());
        timelineArea.setCaretPosition(0);

        // Enable buttons based on status
        boolean isResolved = (c.getStatus() == ComplaintStatus.RESOLVED || c.getStatus() == ComplaintStatus.CLOSED);
        feedbackBtn.setEnabled(isResolved);
        addInfoBtn.setEnabled(!c.getStatus().isTerminal());
    }

    private void handleFeedback() {
        if (selectedComplaint == null) return;
        FeedbackDialog dlg = new FeedbackDialog(parentFrame, selectedComplaint, this::refreshData);
        dlg.setVisible(true);
    }

    private void handleAddInfo() {
        if (selectedComplaint == null) return;
        String info = JOptionPane.showInputDialog(this,
                "Enter additional details or clarifications for ticket #" + selectedComplaint.getTicketNumber() + ":",
                "Supplementary Information", JOptionPane.PLAIN_MESSAGE);

        if (info != null && !info.trim().isEmpty()) {
            try {
                AppController.getInstance().getCreditWalletService().awardCredits(
                        studentId,
                        selectedComplaint.getComplaintId(),
                        TransactionType.SUPPLEMENTARY_INFO,
                        "Supplementary info added for ticket #" + selectedComplaint.getTicketNumber()
                );
                // Update description
                selectedComplaint.setDescription(selectedComplaint.getDescription() + "\n[Update: " + info.trim() + "]");
                AppController.getInstance().getComplaintService().updateProgress(selectedComplaint.getComplaintId(), "Student", "Added details: " + info.trim());
                JOptionPane.showMessageDialog(this, "Supplementary information recorded! +3 credits awarded.", "Reward Earned", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
