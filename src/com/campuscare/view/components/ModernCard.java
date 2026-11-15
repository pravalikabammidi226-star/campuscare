package com.campuscare.view.components;

import com.campuscare.util.UITheme;
import java.awt.*;
import javax.swing.JPanel;

/**
 * Modern card container panel featuring rounded borders, white surface, and gentle styling.
 */
public class ModernCard extends JPanel {

    private int cornerRadius = 10;
    private Color backgroundColor = UITheme.BG_CARD;
    private Color borderColor = UITheme.BORDER_LIGHT;

    public ModernCard() {
        this(new BorderLayout());
    }

    public ModernCard(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(UITheme.createCardBorder());
    }

    public ModernCard(int padding) {
        super(new BorderLayout());
        setOpaque(false);
        setBorder(UITheme.createPadding(padding, padding, padding, padding));
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill background
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        // Draw border
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}
