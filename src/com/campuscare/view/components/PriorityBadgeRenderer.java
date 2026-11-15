package com.campuscare.view.components;

import com.campuscare.model.enums.PriorityLevel;
import com.campuscare.util.UITheme;
import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 * Custom table cell renderer drawing styled priority pills (Normal, High, Urgent).
 */
public class PriorityBadgeRenderer extends JLabel implements TableCellRenderer {

    private Color badgeColor = UITheme.PRIORITY_NORMAL;

    public PriorityBadgeRenderer() {
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(UITheme.FONT_BADGE);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        if (value instanceof PriorityLevel) {
            PriorityLevel p = (PriorityLevel) value;
            setText(p.getDisplayName());
            badgeColor = getColorForPriority(p);
        } else if (value != null) {
            setText(value.toString());
            try {
                PriorityLevel pl = PriorityLevel.valueOf(value.toString().toUpperCase());
                badgeColor = getColorForPriority(pl);
            } catch (Exception e) {
                badgeColor = UITheme.TEXT_MUTED;
            }
        } else {
            setText("");
        }
        return this;
    }

    private Color getColorForPriority(PriorityLevel p) {
        switch (p) {
            case URGENT: return UITheme.PRIORITY_URGENT;
            case HIGH: return UITheme.PRIORITY_HIGH;
            case NORMAL:
            default: return UITheme.PRIORITY_NORMAL;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Soft pastel pill background
        Color bg = new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 35);
        g2.setColor(bg);
        g2.fillRoundRect(8, 6, getWidth() - 16, getHeight() - 12, 12, 12);

        // Badge border
        g2.setColor(badgeColor);
        g2.drawRoundRect(8, 6, getWidth() - 16, getHeight() - 12, 12, 12);

        setForeground(badgeColor.darker());
        g2.dispose();
        super.paintComponent(g);
    }
}
