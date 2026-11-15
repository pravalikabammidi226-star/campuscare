package com.campuscare.view.components;

import com.campuscare.util.UITheme;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * Custom modern Swing button with rounded corners, subtle hover elevation, and brand styling.
 */
public class ModernButton extends JButton {

    private Color normalBackground;
    private Color hoverBackground;
    private Color normalForeground;
    private int cornerRadius = 8;
    private boolean isHovered = false;

    public ModernButton(String text) {
        this(text, UITheme.PRIMARY_ACCENT, Color.WHITE);
    }

    public ModernButton(String text, Color bg, Color fg) {
        super(text);
        this.normalBackground = bg;
        this.hoverBackground = bg.darker();
        this.normalForeground = fg;

        setFont(UITheme.FONT_BOLD);
        setForeground(fg);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(8, 16, 8, 16));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    public static ModernButton secondary(String text) {
        ModernButton btn = new ModernButton(text, new Color(0xE2, 0xE8, 0xF0), UITheme.TEXT_PRIMARY);
        btn.hoverBackground = new Color(0xCB, 0xD5, 0xE1);
        return btn;
    }

    public static ModernButton danger(String text) {
        return new ModernButton(text, UITheme.PRIORITY_URGENT, Color.WHITE);
    }

    public static ModernButton success(String text) {
        return new ModernButton(text, UITheme.COLOR_RESOLVED, Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = isHovered ? hoverBackground : normalBackground;
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}
