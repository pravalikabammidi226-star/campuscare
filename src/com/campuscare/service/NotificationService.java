package com.campuscare.service;

import com.campuscare.dao.DaoFactory;
import com.campuscare.dao.NotificationDao;
import com.campuscare.model.Notification;
import com.campuscare.model.enums.UserRole;
import java.util.List;

/**
 * Service managing user notifications and read receipt status.
 */
public class NotificationService {

    private final NotificationDao notificationDao;

    public NotificationService() {
        this.notificationDao = DaoFactory.getInstance().getNotificationDao();
    }

    public NotificationService(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    public List<Notification> getUserNotifications(String userId, boolean unreadOnly) {
        if (userId == null) return List.of();
        return notificationDao.findByRecipient(userId, unreadOnly);
    }

    public void sendNotification(String recipientId, UserRole role, String title, String message) {
        if (recipientId != null && title != null && message != null) {
            Notification n = new Notification(recipientId, role, title, message);
            notificationDao.save(n);
        }
    }

    public boolean markAsRead(int notificationId) {
        return notificationDao.markAsRead(notificationId);
    }

    public boolean markAllAsRead(String userId) {
        if (userId == null) return false;
        return notificationDao.markAllAsRead(userId);
    }

    public int getUnreadCount(String userId) {
        return getUserNotifications(userId, true).size();
    }
}
