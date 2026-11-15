package com.campuscare.service;

import com.campuscare.dao.ComplaintDao;
import com.campuscare.dao.ComplaintHistoryDao;
import com.campuscare.dao.DaoFactory;
import com.campuscare.dao.NotificationDao;
import com.campuscare.model.Complaint;
import com.campuscare.model.ComplaintHistory;
import com.campuscare.model.Notification;
import com.campuscare.model.enums.ComplaintStatus;
import com.campuscare.model.enums.PriorityLevel;
import com.campuscare.model.enums.UserRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service managing automated complaint escalation for SLA breaches.
 */
public class EscalationService {

    private static final Logger LOGGER = Logger.getLogger(EscalationService.class.getName());

    private final ComplaintDao complaintDao;
    private final ComplaintHistoryDao historyDao;
    private final NotificationDao notificationDao;

    public EscalationService() {
        DaoFactory factory = DaoFactory.getInstance();
        this.complaintDao = factory.getComplaintDao();
        this.historyDao = factory.getComplaintHistoryDao();
        this.notificationDao = factory.getNotificationDao();
    }

    public EscalationService(ComplaintDao complaintDao, ComplaintHistoryDao historyDao, NotificationDao notificationDao) {
        this.complaintDao = complaintDao;
        this.historyDao = historyDao;
        this.notificationDao = notificationDao;
    }

    /**
     * Checks all active complaints for SLA breaches and escalates overdue tickets.
     * @return Number of complaints escalated during this check
     */
    public synchronized int checkAndEscalateOverdueComplaints() {
        List<Complaint> overdueComplaints = complaintDao.findOverdueComplaints();
        int escalatedCount = 0;

        for (Complaint c : overdueComplaints) {
            if (c.getStatus() != ComplaintStatus.ESCALATED) {
                ComplaintStatus oldStatus = c.getStatus();
                c.setStatus(ComplaintStatus.ESCALATED);
                c.setPriority(PriorityLevel.URGENT);

                // Update database record
                complaintDao.update(c);

                // Record audit trail
                ComplaintHistory history = new ComplaintHistory(
                        c.getComplaintId(),
                        oldStatus.name(),
                        ComplaintStatus.ESCALATED.name(),
                        "Automated SLA breach: Unresolved past deadline (" + c.getDeadlineAt() + "). Escalated to campus administration.",
                        "System Escalation Daemon",
                        "SYSTEM"
                );
                historyDao.save(history);

                // Send notification to Admin
                Notification adminNotif = new Notification(
                        "ADM-001",
                        UserRole.ADMIN,
                        "SLA BREACH ALERT: Ticket " + c.getTicketNumber(),
                        "Complaint \"" + c.getTitle() + "\" at " + c.getLocation() + " is overdue and has been escalated!"
                );
                notificationDao.save(adminNotif);

                // Send notification to assigned staff if any
                if (c.getAssignedStaffId() != null) {
                    Notification staffNotif = new Notification(
                            c.getAssignedStaffId(),
                            UserRole.STAFF,
                            "Urgent Ticket Escalated (" + c.getTicketNumber() + ")",
                            "Ticket assigned to you has exceeded the resolution deadline and is escalated to the Chief Grievance Officer."
                    );
                    notificationDao.save(staffNotif);
                }

                escalatedCount++;
                LOGGER.warning("Ticket Escalated: " + c.getTicketNumber() + " (was " + oldStatus + ")");
            }
        }
        return escalatedCount;
    }
}
