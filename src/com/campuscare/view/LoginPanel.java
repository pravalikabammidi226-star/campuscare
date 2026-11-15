package com.campuscare.view;

import com.campuscare.controller.AppController;
import com.campuscare.exception.AuthenticationException;
import com.campuscare.model.Admin;
import com.campuscare.model.DepartmentStaff;
import com.campuscare.model.Student;
import com.campuscare.model.enums.UserRole;
import com.campuscare.util.UITheme;
import com.campuscare.view.components.ModernButton;
import com.campuscare.view.components.ModernCard;
import java.awt.*;
import javax.swing.*;

/**
 * Modern login interface providing tabbed role-based authentication into Student,
 * Administrator, and Department Staff portals with quick demo account presets.
 */
public class LoginPanel extends JPanel {

    private final MainFrame mainFrame;
    private UserRole selectedRole = UserRole.STUDENT;

    private JLabel roleTitleLabel;
    private JLabel identifierLabel;
    private JTextField identifierField;
    private JPasswordField passwordField;
    private ModernButton studentTabBtn;
    private ModernButton adminTabBtn;
    private ModernButton staffTabBtn;
    private ModernButton registerBtn;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());
        setBackground(UITheme.BG_MAIN);

        initUI();
        switchRole(UserRole.STUDENT);
    }

    private void initUI() {
        JPanel card = new ModernCard(new BorderLayout(16, 16));
        card.setPreferredSize(new Dimension(500, 580));
        card.setBorder(UITheme.createPadding(26, 32, 26, 32));

        // Header Title
        JPanel header = new JPanel(new GridLayout(3, 1, 2, 2));
        header.setOpaque(false);

        JLabel brand = new JLabel("CampusCare+", SwingConstants.CENTER);
        brand.setFont(new Font("SansSerif", Font.BOLD, 28));
        brand.setForeground(UITheme.PRIMARY_ACCENT);

        JLabel subtitle = new JLabel("Smart Campus Complaint & Grievance System", SwingConstants.CENTER);
        subtitle.setFont(UITheme.FONT_HEADER);
        subtitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel desc = new JLabel("Student Credit & Participation Reward Platform", SwingConstants.CENTER);
        desc.setFont(UITheme.FONT_SMALL);
        desc.setForeground(UITheme.TEXT_MUTED);

        header.add(brand);
        header.add(subtitle);
        header.add(desc);
        card.add(header, BorderLayout.NORTH);

        // Center Content
        JPanel center = new JPanel(new BorderLayout(14, 14));
        center.setOpaque(false);

        // Portal Selector Buttons
        JPanel portalTabs = new JPanel(new GridLayout(1, 3, 6, 0));
        portalTabs.setOpaque(false);

        studentTabBtn = new ModernButton("🎓 Student");
        studentTabBtn.addActionListener(e -> switchRole(UserRole.STUDENT));

        adminTabBtn = ModernButton.secondary("🛡️ Admin");
        adminTabBtn.addActionListener(e -> switchRole(UserRole.ADMIN));

        staffTabBtn = ModernButton.secondary("🔧 Staff");
        staffTabBtn.addActionListener(e -> switchRole(UserRole.STAFF));

        portalTabs.add(studentTabBtn);
        portalTabs.add(adminTabBtn);
        portalTabs.add(staffTabBtn);
        center.add(portalTabs, BorderLayout.NORTH);

        // Credentials Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 0, 6, 0);
        int r = 0;

        roleTitleLabel = new JLabel("Student Portal Login");
        roleTitleLabel.setFont(UITheme.FONT_SUBHEADER);
        roleTitleLabel.setForeground(UITheme.PRIMARY_ACCENT);
        gbc.gridx = 0; gbc.gridy = r++;
        form.add(roleTitleLabel, gbc);

        // Quick Demo Accounts Dropdown
        gbc.gridy = r++;
        form.add(new JLabel("Quick Demo Account Presets:"), gbc);
        gbc.gridy = r++;
        String[] presets = {
                "-- Select a Demo Account --",
                "Student: Aarav Mehta (22CS0101)",
                "Student: Diya Nair (22EC0204)",
                "Student: Rohan Gupta (23ME0305)",
                "Admin: Prof. Arvind Sharma (admin)",
                "Staff: Suresh Patel - Electrical (EMP-ELEC-01)",
                "Staff: Ramesh Kumar - Hostel (EMP-HOSTEL-01)",
                "Staff: Pooja Verma - IT & Wi-Fi (EMP-IT-01)"
        };
        JComboBox<String> presetCombo = new JComboBox<>(presets);
        presetCombo.setFont(UITheme.FONT_BODY);
        presetCombo.addActionListener(e -> applyPreset(presetCombo.getSelectedIndex()));
        form.add(presetCombo, gbc);

        // Identifier
        gbc.gridy = r++;
        identifierLabel = new JLabel("Roll Number / Email *");
        identifierLabel.setFont(UITheme.FONT_BOLD);
        form.add(identifierLabel, gbc);

        gbc.gridy = r++;
        identifierField = new JTextField();
        identifierField.setFont(UITheme.FONT_BODY);
        identifierField.setPreferredSize(new Dimension(identifierField.getWidth(), 34));
        form.add(identifierField, gbc);

        // Password
        gbc.gridy = r++;
        JLabel passLabel = new JLabel("Password *");
        passLabel.setFont(UITheme.FONT_BOLD);
        form.add(passLabel, gbc);

        gbc.gridy = r++;
        passwordField = new JPasswordField();
        passwordField.setFont(UITheme.FONT_BODY);
        passwordField.setPreferredSize(new Dimension(passwordField.getWidth(), 34));
        form.add(passwordField, gbc);

        center.add(form, BorderLayout.CENTER);
        card.add(center, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new GridLayout(2, 1, 8, 8));
        footer.setOpaque(false);

        ModernButton loginBtn = new ModernButton("Sign In to Portal");
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginBtn.setPreferredSize(new Dimension(loginBtn.getWidth(), 38));
        loginBtn.addActionListener(e -> handleLogin());

        registerBtn = ModernButton.secondary("Register New Student Account");
        registerBtn.addActionListener(e -> {
            StudentRegistrationDialog dlg = new StudentRegistrationDialog(mainFrame, () -> {});
            dlg.setVisible(true);
        });

        footer.add(loginBtn);
        footer.add(registerBtn);
        card.add(footer, BorderLayout.SOUTH);

        add(card);
    }

    private void switchRole(UserRole role) {
        this.selectedRole = role;
        identifierField.setText("");
        passwordField.setText("");

        if (role == UserRole.STUDENT) {
            roleTitleLabel.setText("Student Portal Login");
            identifierLabel.setText("Roll Number / Campus Email *");
            registerBtn.setVisible(true);
            studentTabBtn.setBackground(UITheme.PRIMARY_ACCENT);
            studentTabBtn.setForeground(Color.WHITE);
            adminTabBtn.setBackground(new Color(0xE2, 0xE8, 0xF0));
            adminTabBtn.setForeground(UITheme.TEXT_PRIMARY);
            staffTabBtn.setBackground(new Color(0xE2, 0xE8, 0xF0));
            staffTabBtn.setForeground(UITheme.TEXT_PRIMARY);
            identifierField.setText("22CS0101");
            passwordField.setText("password123");
        } else if (role == UserRole.ADMIN) {
            roleTitleLabel.setText("Campus Administrator Portal Login");
            identifierLabel.setText("Admin Username / Email *");
            registerBtn.setVisible(false);
            studentTabBtn.setBackground(new Color(0xE2, 0xE8, 0xF0));
            studentTabBtn.setForeground(UITheme.TEXT_PRIMARY);
            adminTabBtn.setBackground(UITheme.PRIMARY_ACCENT);
            adminTabBtn.setForeground(Color.WHITE);
            staffTabBtn.setBackground(new Color(0xE2, 0xE8, 0xF0));
            staffTabBtn.setForeground(UITheme.TEXT_PRIMARY);
            identifierField.setText("admin");
            passwordField.setText("admin123");
        } else {
            roleTitleLabel.setText("Department Staff Portal Login");
            identifierLabel.setText("Employee Code / Email *");
            registerBtn.setVisible(false);
            studentTabBtn.setBackground(new Color(0xE2, 0xE8, 0xF0));
            studentTabBtn.setForeground(UITheme.TEXT_PRIMARY);
            adminTabBtn.setBackground(new Color(0xE2, 0xE8, 0xF0));
            adminTabBtn.setForeground(UITheme.TEXT_PRIMARY);
            staffTabBtn.setBackground(UITheme.PRIMARY_ACCENT);
            staffTabBtn.setForeground(Color.WHITE);
            identifierField.setText("EMP-ELEC-01");
            passwordField.setText("staff123");
        }
        revalidate();
        repaint();
    }

    private void applyPreset(int index) {
        switch (index) {
            case 1:
                switchRole(UserRole.STUDENT);
                identifierField.setText("22CS0101");
                passwordField.setText("password123");
                break;
            case 2:
                switchRole(UserRole.STUDENT);
                identifierField.setText("22EC0204");
                passwordField.setText("password123");
                break;
            case 3:
                switchRole(UserRole.STUDENT);
                identifierField.setText("23ME0305");
                passwordField.setText("password123");
                break;
            case 4:
                switchRole(UserRole.ADMIN);
                identifierField.setText("admin");
                passwordField.setText("admin123");
                break;
            case 5:
                switchRole(UserRole.STAFF);
                identifierField.setText("EMP-ELEC-01");
                passwordField.setText("staff123");
                break;
            case 6:
                switchRole(UserRole.STAFF);
                identifierField.setText("EMP-HOSTEL-01");
                passwordField.setText("staff123");
                break;
            case 7:
                switchRole(UserRole.STAFF);
                identifierField.setText("EMP-IT-01");
                passwordField.setText("staff123");
                break;
            default:
                break;
        }
    }

    private void handleLogin() {
        String identifier = identifierField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            if (selectedRole == UserRole.STUDENT) {
                Student s = AppController.getInstance().getAuthService().loginStudent(identifier, password);
                AppController.getInstance().setCurrentUser(s);
                mainFrame.showStudentDashboard(s);
            } else if (selectedRole == UserRole.ADMIN) {
                Admin a = AppController.getInstance().getAuthService().loginAdmin(identifier, password);
                AppController.getInstance().setCurrentUser(a);
                mainFrame.showAdminDashboard(a);
            } else {
                DepartmentStaff stf = AppController.getInstance().getAuthService().loginStaff(identifier, password);
                AppController.getInstance().setCurrentUser(stf);
                mainFrame.showStaffDashboard(stf);
            }
        } catch (AuthenticationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Authentication Failed", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
