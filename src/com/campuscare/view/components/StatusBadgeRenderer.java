package com.campuscare.view.components;

import com.campuscare.model.enums.ComplaintStatus;
import com.campuscare.util.UITheme;
import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 * Custom table cell renderer drawing styled status badges.
 */
public class StatusBadgeRenderer extends JLabel implements TableCellRenderer {

    private Color badgeColor = UITheme.COLOR_SUBMITTED;

    public StatusBadgeRenderer() {
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(UITheme.FONT_BADGE);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        if (value instanceof ComplaintStatus) {
            ComplaintStatus status = (ComplaintStatus) value;
            setText(status.getDisplayName());
            badgeColor = getColorForStatus(status);
        } else if (value != null) {
            setText(value.toString());
            try {
                ComplaintStatus st = ComplaintStatus.valueOf(value.toString().toUpperCase().replace(" ", "_"));
                badgeColor = getColorForStatus(st);
            } catch (Exception e) {
                badgeColor = UITheme.TEXT_MUTED;
            }
        } else {
            setText("");
        }
        return this;
    }

    private Color getColorForStatus(ComplaintStatus status) {
        switch (status) {
            case SUBMITTED: return UITheme.COLOR_SUBMITTED;
            case ASSIGNED: return UITheme.COLOR_ASSIGNED;
            case IN_PROGRESS: return UITheme.COLOR_IN_PROGRESS;
            case RESOLVED: return UITheme.COLOR_RESOLVED;
            case ESCALATED: return UITheme.COLOR_ESCALATED;
            case REJECTED: return UITheme.COLOR_REJECTED;
            case CLOSED: return UITheme.COLOR_CLOSED;
            default: return UITheme.TEXT_MUTED;
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
