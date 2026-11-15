package com.campuscare.view;

import com.campuscare.controller.AppController;
import com.campuscare.exception.ValidationException;
import com.campuscare.model.Student;
import com.campuscare.util.UITheme;
import com.campuscare.view.components.ModernButton;
import com.campuscare.view.components.ModernCard;
import java.awt.*;
import javax.swing.*;

/**
 * Dialog for student self-registration into CampusCare+.
 */
public class StudentRegistrationDialog extends JDialog {

    private final Runnable onSuccess;

    private JTextField rollField;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passField;
    private JTextField phoneField;
    private JComboBox<String> deptCombo;
    private JSpinner semSpinner;

    public StudentRegistrationDialog(Frame owner, Runnable onSuccess) {
        super(owner, "Student Registration - CampusCare+", true);
        this.onSuccess = onSuccess;

        setSize(480, 560);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_MAIN);

        initUI();
    }

    private void initUI() {
        JPanel card = new ModernCard(new BorderLayout(14, 14));
        card.setBorder(UITheme.createPadding(20, 24, 20, 24));

        JPanel header = new JPanel(new GridLayout(2, 1, 2, 2));
        header.setOpaque(false);
        JLabel title = new JLabel("Create Student Account");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Register to report campus grievances and earn participation credits.");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_SECONDARY);

        header.add(title);
        header.add(sub);
        card.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);
        int r = 0;

        // Roll Number
        gbc.gridx = 0; gbc.gridy = r++;
        form.add(new JLabel("University Roll Number *"), gbc);
        gbc.gridy = r++;
        rollField = new JTextField();
        rollField.setFont(UITheme.FONT_BODY);
        rollField.setPreferredSize(new Dimension(rollField.getWidth(), 30));
        form.add(rollField, gbc);

        // Name
        gbc.gridy = r++;
        form.add(new JLabel("Full Name *"), gbc);
        gbc.gridy = r++;
        nameField = new JTextField();
        nameField.setFont(UITheme.FONT_BODY);
        nameField.setPreferredSize(new Dimension(nameField.getWidth(), 30));
        form.add(nameField, gbc);

        // Email
        gbc.gridy = r++;
        form.add(new JLabel("Campus / Personal Email *"), gbc);
        gbc.gridy = r++;
        emailField = new JTextField();
        emailField.setFont(UITheme.FONT_BODY);
        emailField.setPreferredSize(new Dimension(emailField.getWidth(), 30));
        form.add(emailField, gbc);

        // Password
        gbc.gridy = r++;
        form.add(new JLabel("Password (min 6 characters) *"), gbc);
        gbc.gridy = r++;
        passField = new JPasswordField();
        passField.setFont(UITheme.FONT_BODY);
        passField.setPreferredSize(new Dimension(passField.getWidth(), 30));
        form.add(passField, gbc);

        // Phone
        gbc.gridy = r++;
        form.add(new JLabel("Contact Phone Number"), gbc);
        gbc.gridy = r++;
        phoneField = new JTextField();
        phoneField.setFont(UITheme.FONT_BODY);
        phoneField.setPreferredSize(new Dimension(phoneField.getWidth(), 30));
        form.add(phoneField, gbc);

        // Department & Semester Row
        JPanel deptSemRow = new JPanel(new GridLayout(1, 2, 10, 0));
        deptSemRow.setOpaque(false);

        JPanel deptBox = new JPanel(new BorderLayout(2, 2));
        deptBox.setOpaque(false);
        deptBox.add(new JLabel("Department *"), BorderLayout.NORTH);
        String[] depts = {
                "Computer Science & Engineering",
                "Electronics & Communication",
                "Electrical & Electronics",
                "Mechanical Engineering",
                "Civil Engineering",
                "Information Technology",
                "Biotechnology"
        };
        deptCombo = new JComboBox<>(depts);
        deptCombo.setFont(UITheme.FONT_BODY);
        deptBox.add(deptCombo, BorderLayout.CENTER);

        JPanel semBox = new JPanel(new BorderLayout(2, 2));
        semBox.setOpaque(false);
        semBox.add(new JLabel("Semester (1-8) *"), BorderLayout.NORTH);
        semSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
        semSpinner.setFont(UITheme.FONT_BODY);
        semBox.add(semSpinner, BorderLayout.CENTER);

        deptSemRow.add(deptBox);
        deptSemRow.add(semBox);

        gbc.gridy = r++;
        form.add(deptSemRow, gbc);

        card.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);

        ModernButton cancelBtn = ModernButton.secondary("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        ModernButton registerBtn = new ModernButton("Register Account");
        registerBtn.addActionListener(e -> handleRegister());

        footer.add(cancelBtn);
        footer.add(registerBtn);
        card.add(footer, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    private void handleRegister() {
        String roll = rollField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passField.getPassword());
        String phone = phoneField.getText().trim();
        String dept = (String) deptCombo.getSelectedItem();
        int sem = (Integer) semSpinner.getValue();

        try {
            Student s = AppController.getInstance().getAuthService().registerStudent(
                    roll, name, email, password, phone, dept, sem
            );

            JOptionPane.showMessageDialog(this,
                    "Account registered successfully!\n\nWelcome " + s.getName() + "!\nCampusCare Credit Wallet WLT-" + s.getId() + " initialized with 0 pts.",
                    "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

            if (onSuccess != null) onSuccess.run();
            dispose();

        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
