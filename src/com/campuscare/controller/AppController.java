package com.campuscare.controller;

import com.campuscare.model.User;
import com.campuscare.service.*;
import com.campuscare.view.MainFrame;

/**
 * Central controller coordinating user sessions, business services, and view state transitions.
 * Implements the Mediator / Front Controller pattern in Swing.
 */
public class AppController {

    private static AppController instance;

    private final AuthService authService;
    private final ComplaintService complaintService;
    private final CreditWalletService creditWalletService;
    private final DepartmentService departmentService;
    private final FeedbackService feedbackService;
    private final NotificationService notificationService;
    private final AnalyticsService analyticsService;
    private final ReportExportService reportExportService;
    private final BackgroundEscalationDaemon escalationDaemon;

    private User currentUser;
    private MainFrame mainFrame;

    private AppController() {
        this.authService = new AuthService();
        this.complaintService = new ComplaintService();
        this.creditWalletService = new CreditWalletService();
        this.departmentService = new DepartmentService();
        this.feedbackService = new FeedbackService();
        this.notificationService = new NotificationService();
        this.analyticsService = new AnalyticsService();
        this.reportExportService = new ReportExportService();
        this.escalationDaemon = new BackgroundEscalationDaemon();
    }

    public static synchronized AppController getInstance() {
        if (instance == null) {
            instance = new AppController();
        }
        return instance;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
        if (mainFrame != null) {
            mainFrame.showLogin();
        }
    }

    public void startBackgroundTasks() {
        escalationDaemon.start();
    }

    public void stopBackgroundTasks() {
        escalationDaemon.stop();
    }

    public AuthService getAuthService() { return authService; }
    public ComplaintService getComplaintService() { return complaintService; }
    public CreditWalletService getCreditWalletService() { return creditWalletService; }
    public DepartmentService getDepartmentService() { return departmentService; }
    public FeedbackService getFeedbackService() { return feedbackService; }
    public NotificationService getNotificationService() { return notificationService; }
    public AnalyticsService getAnalyticsService() { return analyticsService; }
    public ReportExportService getReportExportService() { return reportExportService; }
}
