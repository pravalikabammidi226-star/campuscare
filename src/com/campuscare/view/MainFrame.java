package com.campuscare.view;

import com.campuscare.controller.AppController;
import com.campuscare.model.Admin;
import com.campuscare.model.DepartmentStaff;
import com.campuscare.model.Student;
import com.campuscare.view.admin.AdminDashboardPanel;
import com.campuscare.view.staff.StaffDashboardPanel;
import com.campuscare.view.student.StudentDashboardPanel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 * Main application window managing CardLayout navigation across Login, Student, Admin,
 * and Department Staff portals.
 */
public class MainFrame extends JFrame {

    public static final String CARD_LOGIN = "LOGIN";
    public static final String CARD_STUDENT = "STUDENT";
    public static final String CARD_ADMIN = "ADMIN";
    public static final String CARD_STAFF = "STAFF";

    private final CardLayout cardLayout;
    private final JPanel cardsContainer;

    private LoginPanel loginPanel;
    private StudentDashboardPanel studentDashboard;
    private AdminDashboardPanel adminDashboard;
    private StaffDashboardPanel staffDashboard;

    public MainFrame() {
        super("CampusCare+ – Smart Campus Complaint & Grievance Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 780);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardsContainer = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        cardsContainer.add(loginPanel, CARD_LOGIN);

        add(cardsContainer, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AppController.getInstance().stopBackgroundTasks();
            }
        });

        showLogin();
    }

    public void showLogin() {
        cardLayout.show(cardsContainer, CARD_LOGIN);
        setTitle("CampusCare+ – Portal Sign In");
    }

    public void showStudentDashboard(Student student) {
        if (studentDashboard != null) {
            cardsContainer.remove(studentDashboard);
        }
        studentDashboard = new StudentDashboardPanel(this, student);
        cardsContainer.add(studentDashboard, CARD_STUDENT);
        cardLayout.show(cardsContainer, CARD_STUDENT);
        setTitle("CampusCare+ – Student Portal (" + student.getName() + " - " + student.getRollNumber() + ")");
    }

    public void showAdminDashboard(Admin admin) {
        if (adminDashboard != null) {
            cardsContainer.remove(adminDashboard);
        }
        adminDashboard = new AdminDashboardPanel(this, admin);
        cardsContainer.add(adminDashboard, CARD_ADMIN);
        cardLayout.show(cardsContainer, CARD_ADMIN);
        setTitle("CampusCare+ – Campus Administration Portal (" + admin.getName() + ")");
    }

    public void showStaffDashboard(DepartmentStaff staff) {
        if (staffDashboard != null) {
            cardsContainer.remove(staffDashboard);
        }
        staffDashboard = new StaffDashboardPanel(this, staff);
        cardsContainer.add(staffDashboard, CARD_STAFF);
        cardLayout.show(cardsContainer, CARD_STAFF);
        setTitle("CampusCare+ – Department Staff Portal (" + staff.getName() + " - " + staff.getEmployeeCode() + ")");
    }
}
