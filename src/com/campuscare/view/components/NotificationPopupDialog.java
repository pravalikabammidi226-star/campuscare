package com.campuscare.view.components;

import com.campuscare.model.Notification;
import com.campuscare.service.NotificationService;
import com.campuscare.util.DateTimeUtil;
import com.campuscare.util.UITheme;
import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * Modal dialog displaying the user's recent notifications with read receipts.
 */
public class NotificationPopupDialog extends JDialog {

    private final NotificationService notificationService;
    private final String userId;
    private final Runnable onUpdate;
    private JPanel listPanel;

    public NotificationPopupDialog(Frame owner, String userId, NotificationService notificationService, Runnable onUpdate) {
        super(owner, "Notifications", true);
        this.userId = userId;
        this.notificationService = notificationService;
        this.onUpdate = onUpdate;

        setSize(460, 520);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_MAIN);

        initUI();
    }

    private void initUI() {
        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT),
                UITheme.createPadding(14, 18, 14, 18)
        ));

        JLabel titleLabel = new JLabel("Notification Center");
        titleLabel.setFont(UITheme.FONT_HEADER);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);

        ModernButton markAllBtn = ModernButton.secondary("Mark All as Read");
        markAllBtn.addActionListener(e -> {
            notificationService.markAllAsRead(userId);
            refreshList();
            if (onUpdate != null) onUpdate.run();
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(markAllBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Scrollable List
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UITheme.BG_MAIN);
        listPanel.setBorder(UITheme.createPadding(12, 14, 12, 14));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Close
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT));
        ModernButton closeBtn = ModernButton.secondary("Close");
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        refreshList();
    }

    private void refreshList() {
        listPanel.removeAll();
        List<Notification> notifications = notificationService.getUserNotifications(userId, false);

        if (notifications.isEmpty()) {
            JLabel emptyLabel = new JLabel("No notifications at this time.", SwingConstants.CENTER);
            emptyLabel.setFont(UITheme.FONT_BODY);
            emptyLabel.setForeground(UITheme.TEXT_MUTED);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalStrut(40));
            listPanel.add(emptyLabel);
        } else {
            for (Notification n : notifications) {
                JPanel card = new ModernCard(new BorderLayout(8, 6));
                card.setBackground(n.isRead() ? Color.WHITE : new Color(0xEE, 0xF2, 0xFF));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

                JLabel title = new JLabel(n.getTitle());
                title.setFont(UITheme.FONT_BOLD);
                title.setForeground(n.isRead() ? UITheme.TEXT_PRIMARY : UITheme.PRIMARY_ACCENT);

                JLabel time = new JLabel(DateTimeUtil.toRelativeTime(n.getCreatedAt()));
                time.setFont(UITheme.FONT_SMALL);
                time.setForeground(UITheme.TEXT_MUTED);

                JPanel top = new JPanel(new BorderLayout());
                top.setOpaque(false);
                top.add(title, BorderLayout.WEST);
                top.add(time, BorderLayout.EAST);

                JLabel msg = new JLabel("<html>" + n.getMessage() + "</html>");
                msg.setFont(UITheme.FONT_BODY);
                msg.setForeground(UITheme.TEXT_SECONDARY);

                card.add(top, BorderLayout.NORTH);
                card.add(msg, BorderLayout.CENTER);

                if (!n.isRead()) {
                    ModernButton readBtn = ModernButton.secondary("✓");
                    readBtn.setToolTipText("Mark as Read");
                    readBtn.setPreferredSize(new Dimension(36, 26));
                    readBtn.addActionListener(e -> {
                        notificationService.markAsRead(n.getNotificationId());
                        refreshList();
                        if (onUpdate != null) onUpdate.run();
                    });
                    card.add(readBtn, BorderLayout.EAST);
                }

                listPanel.add(card);
                listPanel.add(Box.createVerticalStrut(8));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }
}
