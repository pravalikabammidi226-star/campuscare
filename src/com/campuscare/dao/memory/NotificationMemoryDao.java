package com.campuscare.dao.memory;

import com.campuscare.dao.NotificationDao;
import com.campuscare.model.Notification;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NotificationMemoryDao implements NotificationDao {
    private final InMemoryStorage storage = InMemoryStorage.getInstance();

    @Override
    public Optional<Notification> findById(Integer id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.notifications.get(id));
    }

    @Override
    public List<Notification> findAll() {
        return new ArrayList<>(storage.notifications.values());
    }

    @Override
    public Notification save(Notification entity) {
        if (entity != null) {
            if (entity.getNotificationId() <= 0) {
                entity.setNotificationId(storage.notificationIdGen.incrementAndGet());
            }
            storage.notifications.put(entity.getNotificationId(), entity);
        }
        return entity;
    }

    @Override
    public boolean update(Notification entity) {
        if (entity != null && storage.notifications.containsKey(entity.getNotificationId())) {
            storage.notifications.put(entity.getNotificationId(), entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        return storage.notifications.remove(id) != null;
    }

    @Override
    public List<Notification> findByRecipient(String userId, boolean unreadOnly) {
        return storage.notifications.values().stream()
                .filter(n -> userId.equals(n.getRecipientUserId()))
                .filter(n -> !unreadOnly || !n.isRead())
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public boolean markAsRead(int notificationId) {
        Notification n = storage.notifications.get(notificationId);
        if (n != null) {
            n.setRead(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean markAllAsRead(String userId) {
        boolean updated = false;
        for (Notification n : storage.notifications.values()) {
            if (userId.equals(n.getRecipientUserId()) && !n.isRead()) {
                n.setRead(true);
                updated = true;
            }
        }
        return updated;
    }
}
