package com.campuscare.dao;

import com.campuscare.dao.jdbc.*;
import com.campuscare.dao.memory.*;
import com.campuscare.util.DatabaseConnectionManager;
import java.util.logging.Logger;

/**
 * Abstract Factory and Singleton for managing Data Access Objects.
 * Seamlessly selects between native MySQL JDBC DAOs and resilient In-Memory/Persistent DAOs.
 * Demonstrates the Factory Method Pattern and Polymorphism.
 */
public class DaoFactory {

    private static final Logger LOGGER = Logger.getLogger(DaoFactory.class.getName());
    private static DaoFactory instance;

    private boolean jdbcActive;

    private StudentDao studentDao;
    private AdminDao adminDao;
    private StaffDao staffDao;
    private DepartmentDao departmentDao;
    private ComplaintDao complaintDao;
    private ComplaintHistoryDao complaintHistoryDao;
    private FeedbackDao feedbackDao;
    private CreditWalletDao creditWalletDao;
    private NotificationDao notificationDao;

    private DaoFactory() {
        initDaos(DatabaseConnectionManager.isDatabaseAvailable());
    }

    public static synchronized DaoFactory getInstance() {
        if (instance == null) {
            instance = new DaoFactory();
        }
        return instance;
    }

    private void initDaos(boolean useJdbc) {
        this.jdbcActive = useJdbc;
        if (useJdbc) {
            LOGGER.info(">>> Initializing CampusCare+ in MySQL JDBC MODE");
            this.studentDao = new StudentJdbcDao();
            this.adminDao = new AdminJdbcDao();
            this.staffDao = new StaffJdbcDao();
            this.departmentDao = new DepartmentJdbcDao();
            this.complaintDao = new ComplaintJdbcDao();
            this.complaintHistoryDao = new ComplaintHistoryJdbcDao();
            this.feedbackDao = new FeedbackJdbcDao();
            this.creditWalletDao = new CreditWalletJdbcDao();
            this.notificationDao = new NotificationJdbcDao();
        } else {
            LOGGER.info(">>> Initializing CampusCare+ in IN-MEMORY PERSISTENT MODE with rich seed data");
            this.studentDao = new StudentMemoryDao();
            this.adminDao = new AdminMemoryDao();
            this.staffDao = new StaffMemoryDao();
            this.departmentDao = new DepartmentMemoryDao();
            this.complaintDao = new ComplaintMemoryDao();
            this.complaintHistoryDao = new ComplaintHistoryMemoryDao();
            this.feedbackDao = new FeedbackMemoryDao();
            this.creditWalletDao = new CreditWalletMemoryDao();
            this.notificationDao = new NotificationMemoryDao();
        }
    }

    public void forceMemoryMode() {
        initDaos(false);
    }

    public void forceJdbcMode() {
        initDaos(true);
    }

    public boolean isJdbcActive() {
        return jdbcActive;
    }

    public StudentDao getStudentDao() { return studentDao; }
    public AdminDao getAdminDao() { return adminDao; }
    public StaffDao getStaffDao() { return staffDao; }
    public DepartmentDao getDepartmentDao() { return departmentDao; }
    public ComplaintDao getComplaintDao() { return complaintDao; }
    public ComplaintHistoryDao getComplaintHistoryDao() { return complaintHistoryDao; }
    public FeedbackDao getFeedbackDao() { return feedbackDao; }
    public CreditWalletDao getCreditWalletDao() { return creditWalletDao; }
    public NotificationDao getNotificationDao() { return notificationDao; }
}
