package com.campuscare.view.components;

import com.campuscare.util.UITheme;
import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

/**
 * Modern styled JTable with alternating row colors, elegant typography, and generous row heights.
 */
public class ModernTable extends JTable {

    public ModernTable(TableModel model) {
        super(model);
        initStyling();
    }

    private void initStyling() {
        setRowHeight(36);
        setFont(UITheme.FONT_BODY);
        setShowGrid(true);
        setGridColor(new Color(0xF1, 0xF5, 0xF9));
        setSelectionBackground(new Color(0xEE, 0xF2, 0xFF));
        setSelectionForeground(UITheme.TEXT_PRIMARY);
        setFillsViewportHeight(true);

        // Header Styling
        JTableHeader header = getTableHeader();
        header.setFont(UITheme.FONT_BOLD);
        header.setBackground(new Color(0xF8, 0xFA, 0xFC));
        header.setForeground(UITheme.TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT));

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
    }

    @Override
    public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (!isRowSelected(row)) {
            c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xFA, 0xFB, 0xFD));
        }
        return c;
    }
}
