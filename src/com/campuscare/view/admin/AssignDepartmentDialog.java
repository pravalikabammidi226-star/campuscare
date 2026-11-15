package com.campuscare.view.admin;

import com.campuscare.controller.AppController;
import com.campuscare.model.Complaint;
import com.campuscare.model.Department;
import com.campuscare.model.DepartmentStaff;
import com.campuscare.util.UITheme;
import com.campuscare.view.components.ModernButton;
import com.campuscare.view.components.ModernCard;
import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * Dialog for administrator to route and assign complaints to departments and staff officers.
 */
public class AssignDepartmentDialog extends JDialog {

    private final Complaint complaint;
    private final String adminName;
    private final Runnable onSuccess;

    private JComboBox<Department> deptCombo;
    private JComboBox<DepartmentStaff> staffCombo;

    public AssignDepartmentDialog(Frame owner, Complaint complaint, String adminName, Runnable onSuccess) {
        super(owner, "Assign Grievance - Ticket #" + complaint.getTicketNumber(), true);
        this.complaint = complaint;
        this.adminName = adminName;
        this.onSuccess = onSuccess;

        setSize(460, 360);
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
        JLabel title = new JLabel("Route Complaint to Department");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("<html>Ticket: <b>#" + complaint.getTicketNumber() + "</b> - " + complaint.getTitle() + "<br>Location: " + complaint.getLocation() + "</html>");
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
        gbc.insets = new Insets(8, 0, 8, 0);
        int r = 0;

        gbc.gridx = 0; gbc.gridy = r++;
        JLabel deptLabel = new JLabel("Select Responsible Department *");
        deptLabel.setFont(UITheme.FONT_BOLD);
        form.add(deptLabel, gbc);

        gbc.gridy = r++;
        List<Department> departments = AppController.getInstance().getDepartmentService().getAllDepartments();
        deptCombo = new JComboBox<>(departments.toArray(new Department[0]));
        deptCombo.setFont(UITheme.FONT_BODY);
        deptCombo.addActionListener(e -> reloadStaff());
        form.add(deptCombo, gbc);

        gbc.gridy = r++;
        JLabel staffLabel = new JLabel("Assign Specific Officer / Staff Member (Optional):");
        staffLabel.setFont(UITheme.FONT_BOLD);
        form.add(staffLabel, gbc);

        gbc.gridy = r++;
        staffCombo = new JComboBox<>();
        staffCombo.setFont(UITheme.FONT_BODY);
        form.add(staffCombo, gbc);

        card.add(form, BorderLayout.CENTER);

        // Preselect suggested department if applicable
        if (complaint.getAssignedDepartmentId() != null) {
            for (int i = 0; i < deptCombo.getItemCount(); i++) {
                if (deptCombo.getItemAt(i).getDepartmentId() == complaint.getAssignedDepartmentId()) {
                    deptCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        reloadStaff();

        // Footer Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        ModernButton cancelBtn = ModernButton.secondary("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        ModernButton assignBtn = new ModernButton("Confirm Assignment");
        assignBtn.addActionListener(e -> handleAssign());

        footer.add(cancelBtn);
        footer.add(assignBtn);
        card.add(footer, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    private void reloadStaff() {
        staffCombo.removeAllItems();
        Department selectedDept = (Department) deptCombo.getSelectedItem();
        if (selectedDept != null) {
            List<DepartmentStaff> staffList = AppController.getInstance().getDepartmentService().getStaffByDepartment(selectedDept.getDepartmentId());
            for (DepartmentStaff s : staffList) {
                staffCombo.addItem(s);
            }
        }
    }

    private void handleAssign() {
        Department dept = (Department) deptCombo.getSelectedItem();
        if (dept == null) {
            JOptionPane.showMessageDialog(this, "Please select a department.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DepartmentStaff stf = (DepartmentStaff) staffCombo.getSelectedItem();
        String staffId = stf != null ? stf.getId() : null;

        try {
            AppController.getInstance().getComplaintService().assignComplaint(
                    complaint.getComplaintId(),
                    dept.getDepartmentId(),
                    staffId,
                    adminName
            );

            JOptionPane.showMessageDialog(this,
                    "Ticket #" + complaint.getTicketNumber() + " successfully assigned to " + dept.getName() +
                            (stf != null ? " (" + stf.getName() + ")" : "") + ".",
                    "Assignment Complete", JOptionPane.INFORMATION_MESSAGE);

            if (onSuccess != null) onSuccess.run();
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Assignment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
