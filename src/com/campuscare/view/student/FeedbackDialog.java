package com.campuscare.view.student;

import com.campuscare.controller.AppController;
import com.campuscare.exception.InvalidComplaintStateException;
import com.campuscare.exception.ValidationException;
import com.campuscare.model.Complaint;
import com.campuscare.util.UITheme;
import com.campuscare.view.components.ModernButton;
import com.campuscare.view.components.ModernCard;
import java.awt.*;
import javax.swing.*;

/**
 * Dialog for student to submit resolution rating and feedback comments.
 */
public class FeedbackDialog extends JDialog {

    private final Complaint complaint;
    private final Runnable onSuccess;
    private JComboBox<Integer> ratingCombo;
    private JTextArea commentsArea;

    public FeedbackDialog(Frame owner, Complaint complaint, Runnable onSuccess) {
        super(owner, "Rate Resolution - " + complaint.getTicketNumber(), true);
        this.complaint = complaint;
        this.onSuccess = onSuccess;

        setSize(460, 380);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_MAIN);

        initUI();
    }

    private void initUI() {
        JPanel card = new ModernCard(new BorderLayout(12, 12));
        card.setBorder(UITheme.createPadding(20, 20, 20, 20));

        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setOpaque(false);
        JLabel title = new JLabel("Resolution Feedback & Rating");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("<html>Ticket: <b>" + complaint.getTicketNumber() + "</b> - " + complaint.getTitle() + "</html>");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_SECONDARY);

        header.add(title);
        header.add(sub);
        card.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel ratingLabel = new JLabel("Rate Resolution Quality (1 - 5 Stars):");
        ratingLabel.setFont(UITheme.FONT_BOLD);
        form.add(ratingLabel, gbc);

        gbc.gridy++;
        Integer[] ratings = {5, 4, 3, 2, 1};
        ratingCombo = new JComboBox<>(ratings);
        ratingCombo.setFont(UITheme.FONT_BODY);
        ratingCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                int stars = (Integer) value;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < stars; i++) sb.append("★ ");
                for (int i = stars; i < 5; i++) sb.append("☆ ");
                l.setText(stars + " - " + sb.toString() + (stars >= 4 ? "(Satisfied)" : "(Needs Improvement)"));
                return l;
            }
        });
        form.add(ratingCombo, gbc);

        gbc.gridy++;
        JLabel commentLabel = new JLabel("Feedback Comments / Resolution Verification:");
        commentLabel.setFont(UITheme.FONT_BOLD);
        form.add(commentLabel, gbc);

        gbc.gridy++;
        commentsArea = new JTextArea(4, 25);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        commentsArea.setFont(UITheme.FONT_BODY);
        JScrollPane scroll = new JScrollPane(commentsArea);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        form.add(scroll, gbc);

        card.add(form, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        ModernButton cancelBtn = ModernButton.secondary("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        ModernButton submitBtn = new ModernButton("Submit & Earn +2 Credits");
        submitBtn.addActionListener(e -> handleSubmit());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(submitBtn);
        card.add(buttonPanel, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    private void handleSubmit() {
        int rating = (Integer) ratingCombo.getSelectedItem();
        String comments = commentsArea.getText().trim();
        try {
            AppController.getInstance().getFeedbackService().submitFeedback(
                    complaint.getComplaintId(),
                    complaint.getStudentId(),
                    rating,
                    comments
            );
            JOptionPane.showMessageDialog(this,
                    "Thank you for your feedback! You have been awarded +2 credits.",
                    "Feedback Submitted", JOptionPane.INFORMATION_MESSAGE);
            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (ValidationException | InvalidComplaintStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Feedback Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
