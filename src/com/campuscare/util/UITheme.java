package com.campuscare.util;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * Modern design system and UI palette for CampusCare+ Swing user interface.
 * Implements an attractive, professional academic-dashboard aesthetic.
 */
public final class UITheme {

    // Brand Colors
    public static final Color PRIMARY_DARK = new Color(0x1E, 0x29, 0x3B);    // Slate Navy
    public static final Color PRIMARY_ACCENT = new Color(0x25, 0x63, 0xEB);  // Royal Blue
    public static final Color PRIMARY_HOVER = new Color(0x1D, 0x4E, 0xD8);   // Darker Blue
    public static final Color ACCENT_TEAL = new Color(0x0D, 0x94, 0x88);     // Teal Accent

    // Neutral Colors
    public static final Color BG_MAIN = new Color(0xF8, 0xFA, 0xFC);         // Crisp background
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BORDER_LIGHT = new Color(0xE2, 0xE8, 0xF0);    // Card borders
    public static final Color TEXT_PRIMARY = new Color(0x0F, 0x17, 0x2A);    // Dark slate text
    public static final Color TEXT_SECONDARY = new Color(0x64, 0x74, 0x8B);  // Muted subtitle
    public static final Color TEXT_MUTED = new Color(0x94, 0xA3, 0xB8);

    // Status Colors
    public static final Color COLOR_SUBMITTED = new Color(0x3B, 0x82, 0xF6); // Blue
    public static final Color COLOR_ASSIGNED = new Color(0x8B, 0x5C, 0xF6);  // Purple
    public static final Color COLOR_IN_PROGRESS = new Color(0xF5, 0x9E, 0x0B);// Amber
    public static final Color COLOR_RESOLVED = new Color(0x10, 0xB9, 0x81);   // Emerald Green
    public static final Color COLOR_ESCALATED = new Color(0xEF, 0x44, 0x44);  // Rose Red
    public static final Color COLOR_REJECTED = new Color(0x6B, 0x72, 0x80);   // Gray
    public static final Color COLOR_CLOSED = new Color(0x47, 0x55, 0x69);     // Slate

    // Priority Colors
    public static final Color PRIORITY_NORMAL = new Color(0x10, 0xB9, 0x81);
    public static final Color PRIORITY_HIGH = new Color(0xF5, 0x9E, 0x0B);
    public static final Color PRIORITY_URGENT = new Color(0xEF, 0x44, 0x44);

    // Typography
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_SUBHEADER = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_BADGE = new Font("SansSerif", Font.BOLD, 11);
    public static final Font FONT_STAT_NUMBER = new Font("SansSerif", Font.BOLD, 26);

    private UITheme() {}

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        );
    }

    public static Border createPadding(int top, int left, int bottom, int right) {
        return BorderFactory.createEmptyBorder(top, left, bottom, right);
    }
}
