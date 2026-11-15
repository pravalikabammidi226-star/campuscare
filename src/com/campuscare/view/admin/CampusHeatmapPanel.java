package com.campuscare.view.admin;

import com.campuscare.controller.AppController;
import com.campuscare.service.AnalyticsService;
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
 * Panel presenting the innovative Text-Based & Graphical Campus Heatmap of grievance hotspots.
 */
public class CampusHeatmapPanel extends JPanel {

    private ModernTable table;
    private DefaultTableModel tableModel;
    private JLabel topHotspotLabel;

    public CampusHeatmapPanel() {
        setLayout(new BorderLayout(14, 14));
        setBackground(UITheme.BG_MAIN);
        setBorder(UITheme.createPadding(14, 16, 14, 16));

        initUI();
        refreshHeatmap();
    }

    private void initUI() {
        JPanel card = new ModernCard(new BorderLayout(12, 12));
        card.setBorder(UITheme.createPadding(16, 18, 16, 18));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 2, 2));
        titleBox.setOpaque(false);
        JLabel title = new JLabel("🗺️ Campus Problem Location Heatmap");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        topHotspotLabel = new JLabel("Top Grievance Zone: Analyzing...");
        topHotspotLabel.setFont(UITheme.FONT_BOLD);
        topHotspotLabel.setForeground(UITheme.PRIORITY_URGENT);

        titleBox.add(title);
        titleBox.add(topHotspotLabel);

        ModernButton refreshBtn = ModernButton.secondary("↻ Refresh Heatmap");
        refreshBtn.addActionListener(e -> refreshHeatmap());

        header.add(titleBox, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        // Heatmap Table
        String[] cols = {"Rank", "Campus Zone / Landmark", "Total Grievances", "Relative Density Bar", "Share %", "Risk Level"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new ModernTable(tableModel);

        // Styling for Text Bar column
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setFont(new Font("Monospaced", Font.BOLD, 13));
                l.setForeground(UITheme.PRIORITY_URGENT);
                return l;
            }
        });

        // Center alignments
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        card.add(scroll, BorderLayout.CENTER);

        // Explanatory Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setOpaque(false);
        JLabel note = new JLabel("<html><font color='#64748B'>* Density bars represent relative complaint volume calculated across all active and resolved campus grievances using Java Streams.</font></html>");
        note.setFont(UITheme.FONT_SMALL);
        footer.add(note);
        card.add(footer, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    public void refreshHeatmap() {
        tableModel.setRowCount(0);
        List<AnalyticsService.LocationDensity> list = AppController.getInstance().getAnalyticsService().getCampusHeatmap();

        if (!list.isEmpty()) {
            topHotspotLabel.setText("Top Problem Area: " + list.get(0).getLocation() + " (" + list.get(0).getCount() + " complaints)");
        } else {
            topHotspotLabel.setText("No complaints logged yet.");
        }

        int rank = 1;
        for (AnalyticsService.LocationDensity loc : list) {
            String riskLevel = "Normal";
            if (loc.getPercentage() >= 30.0) riskLevel = "CRITICAL HOTSPOT";
            else if (loc.getPercentage() >= 15.0) riskLevel = "ELEVATED CONCERN";

            tableModel.addRow(new Object[]{
                    "#" + rank++,
                    loc.getLocation(),
                    loc.getCount() + " tickets",
                    loc.getTextBar(),
                    String.format("%.1f%%", loc.getPercentage()),
                    riskLevel
            });
        }
    }
}
