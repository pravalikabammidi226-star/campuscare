package com.campuscare.view.student;

import com.campuscare.controller.AppController;
import com.campuscare.exception.DuplicateComplaintException;
import com.campuscare.exception.ValidationException;
import com.campuscare.model.Complaint;
import com.campuscare.model.enums.CampusLocation;
import com.campuscare.model.enums.ComplaintCategory;
import com.campuscare.model.enums.PriorityLevel;
import com.campuscare.util.UITheme;
import com.campuscare.view.components.ModernButton;
import com.campuscare.view.components.ModernCard;
import java.awt.*;
import javax.swing.*;

/**
 * Dialog for submitting new complaints with smart category, duplicate detection, and priority heuristic.
 */
public class SubmitComplaintDialog extends JDialog {

    private final String studentId;
    private final Runnable onSuccess;

    private JComboBox<ComplaintCategory> categoryCombo;
    private JTextField titleField;
    private JComboBox<String> locationCombo;
    private JComboBox<PriorityLevel> priorityCombo;
    private JTextArea descriptionArea;
    private JCheckBox anonymousCheck;

    public SubmitComplaintDialog(Frame owner, String studentId, Runnable onSuccess) {
        super(owner, "Submit Campus Complaint / Grievance", true);
        this.studentId = studentId;
        this.onSuccess = onSuccess;

        setSize(540, 620);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_MAIN);

        initUI();
    }

    private void initUI() {
        JPanel container = new ModernCard(new BorderLayout(14, 14));
        container.setBorder(UITheme.createPadding(20, 24, 20, 24));

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1, 2, 2));
        header.setOpaque(false);
        JLabel title = new JLabel("Lodge a Grievance");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Earn +5 reward credits for valid complaints with detailed information.");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.ACCENT_TEAL);

        header.add(title);
        header.add(sub);
        container.add(header, BorderLayout.NORTH);

        // Form Fields
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.weightx = 1.0;
        int row = 0;

        // Category
        gbc.gridx = 0; gbc.gridy = row++;
        JLabel catLabel = new JLabel("Complaint Category *");
        catLabel.setFont(UITheme.FONT_BOLD);
        form.add(catLabel, gbc);

        gbc.gridy = row++;
        categoryCombo = new JComboBox<>(ComplaintCategory.values());
        categoryCombo.setFont(UITheme.FONT_BODY);
        categoryCombo.addActionListener(e -> updateCategoryHints());
        form.add(categoryCombo, gbc);

        // Title
        gbc.gridy = row++;
        JLabel titleLabel = new JLabel("Complaint Title / Summary *");
        titleLabel.setFont(UITheme.FONT_BOLD);
        form.add(titleLabel, gbc);

        gbc.gridy = row++;
        titleField = new JTextField();
        titleField.setFont(UITheme.FONT_BODY);
        titleField.setPreferredSize(new Dimension(titleField.getWidth(), 32));
        form.add(titleField, gbc);

        // Location
        gbc.gridy = row++;
        JLabel locLabel = new JLabel("Campus Location / Specific Spot *");
        locLabel.setFont(UITheme.FONT_BOLD);
        form.add(locLabel, gbc);

        gbc.gridy = row++;
        DefaultComboBoxModel<String> locModel = new DefaultComboBoxModel<>();
        for (CampusLocation loc : CampusLocation.values()) {
            locModel.addElement(loc.getDisplayName());
        }
        locationCombo = new JComboBox<>(locModel);
        locationCombo.setEditable(true);
        locationCombo.setFont(UITheme.FONT_BODY);
        form.add(locationCombo, gbc);

        // Priority Level
        gbc.gridy = row++;
        JLabel prioLabel = new JLabel("Estimated Urgency:");
        prioLabel.setFont(UITheme.FONT_BOLD);
        form.add(prioLabel, gbc);

        gbc.gridy = row++;
        priorityCombo = new JComboBox<>(PriorityLevel.values());
        priorityCombo.setFont(UITheme.FONT_BODY);
        form.add(priorityCombo, gbc);

        // Description
        gbc.gridy = row++;
        JLabel descLabel = new JLabel("Detailed Description (include room no., equipment, or details) *");
        descLabel.setFont(UITheme.FONT_BOLD);
        form.add(descLabel, gbc);

        gbc.gridy = row++;
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(UITheme.FONT_BODY);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        form.add(descScroll, gbc);

        // Anonymous Reporting
        gbc.gridy = row++;
        anonymousCheck = new JCheckBox("Report Anonymously (Identity masked for safety)");
        anonymousCheck.setFont(UITheme.FONT_BOLD);
        anonymousCheck.setForeground(UITheme.PRIMARY_ACCENT);
        anonymousCheck.setOpaque(false);
        form.add(anonymousCheck, gbc);

        container.add(form, BorderLayout.CENTER);

        // Footer Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        ModernButton cancelBtn = ModernButton.secondary("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        ModernButton submitBtn = new ModernButton("Submit Complaint");
        submitBtn.addActionListener(e -> handleSubmit());

        footer.add(cancelBtn);
        footer.add(submitBtn);
        container.add(footer, BorderLayout.SOUTH);

        add(container, BorderLayout.CENTER);
    }

    private void updateCategoryHints() {
        ComplaintCategory cat = (ComplaintCategory) categoryCombo.getSelectedItem();
        if (cat != null && cat.isSensitive()) {
            anonymousCheck.setSelected(true);
            priorityCombo.setSelectedItem(PriorityLevel.URGENT);
        }
    }

    private void handleSubmit() {
        ComplaintCategory category = (ComplaintCategory) categoryCombo.getSelectedItem();
        String title = titleField.getText().trim();
        String location = locationCombo.getSelectedItem() != null ? locationCombo.getSelectedItem().toString().trim() : "";
        PriorityLevel priority = (PriorityLevel) priorityCombo.getSelectedItem();
        String description = descriptionArea.getText().trim();
        boolean isAnonymous = anonymousCheck.isSelected();

        try {
            Complaint submitted = AppController.getInstance().getComplaintService().submitComplaint(
                    studentId,
                    category,
                    title,
                    description,
                    location,
                    priority,
                    isAnonymous
            );

            JOptionPane.showMessageDialog(this,
                    String.format("<html><b>Complaint Registered Successfully!</b><br><br>" +
                                    "Ticket Number: <b>%s</b><br>" +
                                    "Computed Priority: <b>%s</b><br>" +
                                    "Resolution SLA Deadline: <b>%s</b><br><br>" +
                                    "Reward Earned: <b>+5 Credits</b> added to your CampusCare Wallet!</html>",
                            submitted.getTicketNumber(),
                            submitted.getPriority(),
                            submitted.getDeadlineAt()),
                    "Submission Confirmed", JOptionPane.INFORMATION_MESSAGE);

            if (onSuccess != null) onSuccess.run();
            dispose();

        } catch (DuplicateComplaintException dupEx) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "<html><b>Potential Duplicate Warning:</b><br><br>" +
                    dupEx.getMessage() + "<br><br>" +
                    "Do you still wish to submit this complaint as a separate distinct grievance?</html>",
                    "Duplicate Detected (" + Math.round(dupEx.getSimilarityScore() * 100) + "% Match)",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                // If student insists, append unique distinction note and save
                try {
                    Complaint forced = AppController.getInstance().getComplaintService().submitComplaint(
                            studentId,
                            category,
                            "[Re-Submitted] " + title,
                            description + " (Student confirmed distinct from #" + dupEx.getExistingTicketNumber() + ")",
                            location,
                            priority,
                            isAnonymous
                    );
                    JOptionPane.showMessageDialog(this, "Complaint registered as ticket: " + forced.getTicketNumber());
                    if (onSuccess != null) onSuccess.run();
                    dispose();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (ValidationException valEx) {
            JOptionPane.showMessageDialog(this, valEx.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
