package com.campuscare.view.admin;

import com.campuscare.controller.AppController;
import com.campuscare.service.AnalyticsService;
import com.campuscare.util.UITheme;
import com.campuscare.view.components.ModernButton;
import com.campuscare.view.components.ModernCard;
import com.campuscare.view.components.ModernTable;
import java.awt.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Panel presenting department performance rosters, SLA turnaround stats, and file export actions.
 */
public class AnalyticsReportPanel extends JPanel {

    private final Frame parentFrame;
    private ModernTable table;
    private DefaultTableModel tableModel;

    private JLabel avgTimeLabel;
    private JLabel satisfactionLabel;
    private JLabel feedbackCountLabel;

    public AnalyticsReportPanel(Frame parentFrame) {
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout(14, 14));
        setBackground(UITheme.BG_MAIN);
        setBorder(UITheme.createPadding(14, 16, 14, 16));

        initUI();
        refreshMetrics();
    }

    private void initUI() {
        // Top KPI Cards
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 14, 0));
        statsRow.setOpaque(false);

        statsRow.add(createStatCard("AVERAGE RESOLUTION TIME", avgTimeLabel = new JLabel("0.0 hrs"), UITheme.PRIMARY_ACCENT));
        statsRow.add(createStatCard("STUDENT SATISFACTION INDEX", satisfactionLabel = new JLabel("100%"), UITheme.COLOR_RESOLVED));
        statsRow.add(createStatCard("STUDENT RATINGS LOGGED", feedbackCountLabel = new JLabel("0 reviews"), UITheme.COLOR_IN_PROGRESS));

        add(statsRow, BorderLayout.NORTH);

        // Center Department Performance Table
        JPanel card = new ModernCard(new BorderLayout(12, 12));
        card.setBorder(UITheme.createPadding(14, 18, 14, 18));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        JLabel title = new JLabel("Departmental Grievance Resolution Performance");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        ModernButton refreshBtn = ModernButton.secondary("↻ Refresh");
        refreshBtn.addActionListener(e -> refreshMetrics());

        tableHeader.add(title, BorderLayout.WEST);
        tableHeader.add(refreshBtn, BorderLayout.EAST);
        card.add(tableHeader, BorderLayout.NORTH);

        String[] cols = {"Dept ID", "Department Name", "Code", "Assigned", "Resolved", "Pending", "Resolution Rate", "Avg. Hours"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new ModernTable(tableModel);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);
        table.getColumnModel().getColumn(6).setCellRenderer(center);
        table.getColumnModel().getColumn(7).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        card.add(scroll, BorderLayout.CENTER);

        // Footer Export Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setOpaque(false);

        ModernButton csvBtn = new ModernButton("📥 Export Complaints (CSV)");
        csvBtn.addActionListener(e -> handleExportCsv());

        ModernButton txtBtn = ModernButton.secondary("📄 Export Audit Report (TXT)");
        txtBtn.addActionListener(e -> handleExportTxt());

        ModernButton backupBtn = ModernButton.secondary("💾 Serialized System Backup (.dat)");
        backupBtn.addActionListener(e -> handleBackup());

        footer.add(csvBtn);
        footer.add(txtBtn);
        footer.add(backupBtn);
        card.add(footer, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel c = new ModernCard(new BorderLayout(4, 4));
        c.setBorder(UITheme.createPadding(12, 16, 12, 16));

        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_SMALL);
        t.setForeground(UITheme.TEXT_MUTED);

        valueLabel.setFont(UITheme.FONT_STAT_NUMBER);
        valueLabel.setForeground(color);

        c.add(t, BorderLayout.NORTH);
        c.add(valueLabel, BorderLayout.CENTER);
        return c;
    }

    public void refreshMetrics() {
        AnalyticsService.OverallStats stats = AppController.getInstance().getAnalyticsService().getOverallStats();
        avgTimeLabel.setText(String.format("%.1f hrs", stats.getAvgResolutionHours()));
        satisfactionLabel.setText(String.format("%.1f%%", stats.getSatisfactionPercentage()));
        feedbackCountLabel.setText(stats.getFeedbackCount() + " ratings");

        tableModel.setRowCount(0);
        List<AnalyticsService.DepartmentMetric> metrics = AppController.getInstance().getAnalyticsService().getDepartmentMetrics();
        for (AnalyticsService.DepartmentMetric m : metrics) {
            tableModel.addRow(new Object[]{
                    "#" + m.getDepartmentId(),
                    m.getDepartmentName(),
                    m.getDepartmentCode(),
                    m.getTotalAssigned(),
                    m.getResolvedCount(),
                    m.getPendingCount(),
                    String.format("%.1f%%", m.getResolutionRate()),
                    String.format("%.1f hrs", m.getAvgResolutionHours())
            });
        }
    }

    private void handleExportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("campuscare_complaints.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            try {
                int count = AppController.getInstance().getReportExportService().exportComplaintsToCsv(target);
                JOptionPane.showMessageDialog(this,
                        "Successfully exported " + count + " complaints to:\n" + target.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleExportTxt() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("campuscare_department_audit.txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            try {
                AppController.getInstance().getReportExportService().exportDepartmentAuditReport(target);
                JOptionPane.showMessageDialog(this,
                        "Successfully generated departmental audit report at:\n" + target.getAbsolutePath(),
                        "Report Created", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Report export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleBackup() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("campuscare_backup.dat"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            try {
                AppController.getInstance().getReportExportService().serializeBackup(target);
                JOptionPane.showMessageDialog(this,
                        "Successfully created serialized binary backup at:\n" + target.getAbsolutePath(),
                        "Backup Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Backup failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
