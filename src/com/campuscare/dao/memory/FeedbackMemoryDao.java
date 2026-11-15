package com.campuscare.dao.memory;

import com.campuscare.dao.FeedbackDao;
import com.campuscare.model.Feedback;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FeedbackMemoryDao implements FeedbackDao {
    private final InMemoryStorage storage = InMemoryStorage.getInstance();

    @Override
    public Optional<Feedback> findById(Integer id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.feedbacks.get(id));
    }

    @Override
    public List<Feedback> findAll() {
        return new ArrayList<>(storage.feedbacks.values());
    }

    @Override
    public Feedback save(Feedback entity) {
        if (entity != null) {
            if (entity.getFeedbackId() <= 0) {
                entity.setFeedbackId(storage.feedbackIdGen.incrementAndGet());
            }
            storage.feedbacks.put(entity.getFeedbackId(), entity);
        }
        return entity;
    }

    @Override
    public boolean update(Feedback entity) {
        if (entity != null && storage.feedbacks.containsKey(entity.getFeedbackId())) {
            storage.feedbacks.put(entity.getFeedbackId(), entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        return storage.feedbacks.remove(id) != null;
    }

    @Override
    public Optional<Feedback> findByComplaintId(String complaintId) {
        if (complaintId == null) return Optional.empty();
        return storage.feedbacks.values().stream()
                .filter(f -> complaintId.equals(f.getComplaintId()))
                .findFirst();
    }

    @Override
    public List<Feedback> findByStudentId(String studentId) {
        return storage.feedbacks.values().stream()
                .filter(f -> studentId.equals(f.getStudentId()))
                .collect(Collectors.toList());
    }
}
