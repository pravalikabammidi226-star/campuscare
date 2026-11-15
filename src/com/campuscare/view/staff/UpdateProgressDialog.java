package com.campuscare.view.staff;

import com.campuscare.controller.AppController;
import com.campuscare.model.Complaint;
import com.campuscare.util.UITheme;
import com.campuscare.view.components.ModernButton;
import com.campuscare.view.components.ModernCard;
import java.awt.*;
import javax.swing.*;

/**
 * Dialog for department staff to update on-site progress notes and mark ticket In-Progress.
 */
public class UpdateProgressDialog extends JDialog {

    private final Complaint complaint;
    private final String staffName;
    private final Runnable onSuccess;
    private JTextArea notesArea;

    public UpdateProgressDialog(Frame owner, Complaint complaint, String staffName, Runnable onSuccess) {
        super(owner, "Update Progress - #" + complaint.getTicketNumber(), true);
        this.complaint = complaint;
        this.staffName = staffName;
        this.onSuccess = onSuccess;

        setSize(460, 340);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_MAIN);

        initUI();
    }

    private void initUI() {
        JPanel card = new ModernCard(new BorderLayout(12, 12));
        card.setBorder(UITheme.createPadding(18, 20, 18, 20));

        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setOpaque(false);
        JLabel title = new JLabel("Post Work Progress Note");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("<html>Ticket: <b>#" + complaint.getTicketNumber() + "</b> - " + complaint.getTitle() + "</html>");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_SECONDARY);

        header.add(title);
        header.add(sub);
        card.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new BorderLayout(6, 6));
        form.setOpaque(false);
        JLabel notesLabel = new JLabel("Describe Action Taken / Current Inspection Status *");
        notesLabel.setFont(UITheme.FONT_BOLD);

        notesArea = new JTextArea(5, 20);
        notesArea.setFont(UITheme.FONT_BODY);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(notesArea);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));

        form.add(notesLabel, BorderLayout.NORTH);
        form.add(scroll, BorderLayout.CENTER);
        card.add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        ModernButton cancelBtn = ModernButton.secondary("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        ModernButton saveBtn = new ModernButton("Update & Set In-Progress");
        saveBtn.addActionListener(e -> handleSave());

        footer.add(cancelBtn);
        footer.add(saveBtn);
        card.add(footer, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    private void handleSave() {
        String notes = notesArea.getText().trim();
        if (notes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter progress notes.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            AppController.getInstance().getComplaintService().updateProgress(
                    complaint.getComplaintId(),
                    staffName,
                    notes
            );
            JOptionPane.showMessageDialog(this, "Progress note logged successfully.");
            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
