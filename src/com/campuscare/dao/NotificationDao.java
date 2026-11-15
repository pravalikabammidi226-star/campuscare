package com.campuscare.dao;

import com.campuscare.model.Notification;
import java.util.List;

/**
 * Data Access Object interface for Notifications.
 */
public interface NotificationDao extends GenericDao<Notification, Integer> {
    List<Notification> findByRecipient(String userId, boolean unreadOnly);
    boolean markAsRead(int notificationId);
    boolean markAllAsRead(String userId);
}
